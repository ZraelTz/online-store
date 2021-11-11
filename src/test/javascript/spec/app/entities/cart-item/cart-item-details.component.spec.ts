/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import CartItemDetailComponent from '@/entities/cart-item/cart-item-details.vue';
import CartItemClass from '@/entities/cart-item/cart-item-details.component';
import CartItemService from '@/entities/cart-item/cart-item.service';
import router from '@/router';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('CartItem Management Detail Component', () => {
    let wrapper: Wrapper<CartItemClass>;
    let comp: CartItemClass;
    let cartItemServiceStub: SinonStubbedInstance<CartItemService>;

    beforeEach(() => {
      cartItemServiceStub = sinon.createStubInstance<CartItemService>(CartItemService);

      wrapper = shallowMount<CartItemClass>(CartItemDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { cartItemService: () => cartItemServiceStub },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundCartItem = { id: 123 };
        cartItemServiceStub.find.resolves(foundCartItem);

        // WHEN
        comp.retrieveCartItem(123);
        await comp.$nextTick();

        // THEN
        expect(comp.cartItem).toBe(foundCartItem);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundCartItem = { id: 123 };
        cartItemServiceStub.find.resolves(foundCartItem);

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
