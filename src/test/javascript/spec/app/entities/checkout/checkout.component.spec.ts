/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';

import * as config from '@/shared/config/config';
import CheckoutComponent from '@/entities/checkout/checkout.vue';
import CheckoutClass from '@/entities/checkout/checkout.component';
import CheckoutService from '@/entities/checkout/checkout.service';

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
  describe('Checkout Management Component', () => {
    let wrapper: Wrapper<CheckoutClass>;
    let comp: CheckoutClass;
    let checkoutServiceStub: SinonStubbedInstance<CheckoutService>;

    beforeEach(() => {
      checkoutServiceStub = sinon.createStubInstance<CheckoutService>(CheckoutService);
      checkoutServiceStub.retrieve.resolves({ headers: {} });

      wrapper = shallowMount<CheckoutClass>(CheckoutComponent, {
        store,
        i18n,
        localVue,
        stubs: { bModal: bModalStub as any },
        provide: {
          checkoutService: () => checkoutServiceStub,
        },
      });
      comp = wrapper.vm;
    });

    it('Should call load all on init', async () => {
      // GIVEN
      checkoutServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

      // WHEN
      comp.retrieveAllCheckouts();
      await comp.$nextTick();

      // THEN
      expect(checkoutServiceStub.retrieve.called).toBeTruthy();
      expect(comp.checkouts[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
    it('Should call delete service on confirmDelete', async () => {
      // GIVEN
      checkoutServiceStub.delete.resolves({});

      // WHEN
      comp.prepareRemove({ id: 123 });
      comp.removeCheckout();
      await comp.$nextTick();

      // THEN
      expect(checkoutServiceStub.delete.called).toBeTruthy();
      expect(checkoutServiceStub.retrieve.callCount).toEqual(1);
    });
  });
});
