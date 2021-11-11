/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';

import * as config from '@/shared/config/config';
import CheckoutUpdateComponent from '@/entities/checkout/checkout-update.vue';
import CheckoutClass from '@/entities/checkout/checkout-update.component';
import CheckoutService from '@/entities/checkout/checkout.service';

import CartService from '@/entities/cart/cart.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
const router = new Router();
localVue.use(Router);
localVue.component('font-awesome-icon', {});
localVue.component('b-input-group', {});
localVue.component('b-input-group-prepend', {});
localVue.component('b-form-datepicker', {});
localVue.component('b-form-input', {});

describe('Component Tests', () => {
  describe('Checkout Management Update Component', () => {
    let wrapper: Wrapper<CheckoutClass>;
    let comp: CheckoutClass;
    let checkoutServiceStub: SinonStubbedInstance<CheckoutService>;

    beforeEach(() => {
      checkoutServiceStub = sinon.createStubInstance<CheckoutService>(CheckoutService);

      wrapper = shallowMount<CheckoutClass>(CheckoutUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          checkoutService: () => checkoutServiceStub,

          cartService: () => new CartService(),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.checkout = entity;
        checkoutServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(checkoutServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.checkout = entity;
        checkoutServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(checkoutServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundCheckout = { id: 123 };
        checkoutServiceStub.find.resolves(foundCheckout);
        checkoutServiceStub.retrieve.resolves([foundCheckout]);

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
