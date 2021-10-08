/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';

import * as config from '@/shared/config/config';
import VendorComponent from '@/entities/vendor/vendor.vue';
import VendorClass from '@/entities/vendor/vendor.component';
import VendorService from '@/entities/vendor/vendor.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('b-badge', {});
localVue.directive('b-modal', {});
localVue.component('b-button', {});
localVue.component('router-link', {});

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  describe('Vendor Management Component', () => {
    let wrapper: Wrapper<VendorClass>;
    let comp: VendorClass;
    let vendorServiceStub: SinonStubbedInstance<VendorService>;

    beforeEach(() => {
      vendorServiceStub = sinon.createStubInstance<VendorService>(VendorService);
      vendorServiceStub.retrieve.resolves({ headers: {} });

      wrapper = shallowMount<VendorClass>(VendorComponent, {
        store,
        i18n,
        localVue,
        stubs: { bModal: bModalStub as any },
        provide: {
          vendorService: () => vendorServiceStub,
        },
      });
      comp = wrapper.vm;
    });

    it('Should call load all on init', async () => {
      // GIVEN
      vendorServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

      // WHEN
      comp.retrieveAllVendors();
      await comp.$nextTick();

      // THEN
      expect(vendorServiceStub.retrieve.called).toBeTruthy();
      expect(comp.vendors[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
    it('Should call delete service on confirmDelete', async () => {
      // GIVEN
      vendorServiceStub.delete.resolves({});

      // WHEN
      comp.prepareRemove({ id: 123 });
      comp.removeVendor();
      await comp.$nextTick();

      // THEN
      expect(vendorServiceStub.delete.called).toBeTruthy();
      expect(vendorServiceStub.retrieve.callCount).toEqual(1);
    });
  });
});
