/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';

import * as config from '@/shared/config/config';
import CartItemUpdateComponent from '@/entities/cart-item/cart-item-update.vue';
import CartItemClass from '@/entities/cart-item/cart-item-update.component';
import CartItemService from '@/entities/cart-item/cart-item.service';

import ProductService from '@/entities/product/product.service';

import CustomerService from '@/entities/customer/customer.service';

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
  describe('CartItem Management Update Component', () => {
    let wrapper: Wrapper<CartItemClass>;
    let comp: CartItemClass;
    let cartItemServiceStub: SinonStubbedInstance<CartItemService>;

    beforeEach(() => {
      cartItemServiceStub = sinon.createStubInstance<CartItemService>(CartItemService);

      wrapper = shallowMount<CartItemClass>(CartItemUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          cartItemService: () => cartItemServiceStub,

          productService: () => new ProductService(),

          customerService: () => new CustomerService(),

          cartService: () => new CartService(),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.cartItem = entity;
        cartItemServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(cartItemServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.cartItem = entity;
        cartItemServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(cartItemServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundCartItem = { id: 123 };
        cartItemServiceStub.find.resolves(foundCartItem);
        cartItemServiceStub.retrieve.resolves([foundCartItem]);

        // WHEN
        comp.beforeRouteEnter({ params: { cartItemId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.cartItem).toBe(foundCartItem);
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
