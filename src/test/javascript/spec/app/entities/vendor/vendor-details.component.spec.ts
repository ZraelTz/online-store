/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import VendorDetailComponent from '@/entities/vendor/vendor-details.vue';
import VendorClass from '@/entities/vendor/vendor-details.component';
import VendorService from '@/entities/vendor/vendor.service';
import router from '@/router';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Vendor Management Detail Component', () => {
    let wrapper: Wrapper<VendorClass>;
    let comp: VendorClass;
    let vendorServiceStub: SinonStubbedInstance<VendorService>;

    beforeEach(() => {
      vendorServiceStub = sinon.createStubInstance<VendorService>(VendorService);

      wrapper = shallowMount<VendorClass>(VendorDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { vendorService: () => vendorServiceStub },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundVendor = { id: 123 };
        vendorServiceStub.find.resolves(foundVendor);

        // WHEN
        comp.retrieveVendor(123);
        await comp.$nextTick();

        // THEN
        expect(comp.vendor).toBe(foundVendor);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundVendor = { id: 123 };
        vendorServiceStub.find.resolves(foundVendor);

        // WHEN
        comp.beforeRouteEnter({ params: { vendorId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.vendor).toBe(foundVendor);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        comp.previousState();
        await comp.$nextTick();

        expect(comp.$router.currentRoute.fullPath).toContain('/');
      });
    });
  });
});
