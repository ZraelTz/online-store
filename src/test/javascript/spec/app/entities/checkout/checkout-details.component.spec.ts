/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import CheckoutDetailComponent from '@/entities/checkout/checkout-details.vue';
import CheckoutClass from '@/entities/checkout/checkout-details.component';
import CheckoutService from '@/entities/checkout/checkout.service';
import router from '@/router';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Checkout Management Detail Component', () => {
    let wrapper: Wrapper<CheckoutClass>;
    let comp: CheckoutClass;
    let checkoutServiceStub: SinonStubbedInstance<CheckoutService>;

    beforeEach(() => {
      checkoutServiceStub = sinon.createStubInstance<CheckoutService>(CheckoutService);

      wrapper = shallowMount<CheckoutClass>(CheckoutDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { checkoutService: () => checkoutServiceStub },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundCheckout = { id: 123 };
        checkoutServiceStub.find.resolves(foundCheckout);

        // WHEN
        comp.retrieveCheckout(123);
        await comp.$nextTick();

        // THEN
        expect(comp.checkout).toBe(foundCheckout);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundCheckout = { id: 123 };
        checkoutServiceStub.find.resolves(foundCheckout);

        // WHEN
        comp.beforeRouteEnter({ params: { checkoutId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.checkout).toBe(foundCheckout);
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
