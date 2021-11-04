/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import CartDetailComponent from '@/entities/cart/cart-details.vue';
import CartClass from '@/entities/cart/cart-details.component';
import CartService from '@/entities/cart/cart.service';
import router from '@/router';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Cart Management Detail Component', () => {
    let wrapper: Wrapper<CartClass>;
    let comp: CartClass;
    let cartServiceStub: SinonStubbedInstance<CartService>;

    beforeEach(() => {
      cartServiceStub = sinon.createStubInstance<CartService>(CartService);

      wrapper = shallowMount<CartClass>(CartDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { cartService: () => cartServiceStub },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundCart = { id: 123 };
        cartServiceStub.find.resolves(foundCart);

        // WHEN
        comp.retrieveCart(123);
        await comp.$nextTick();

        // THEN
        expect(comp.cart).toBe(foundCart);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundCart = { id: 123 };
        cartServiceStub.find.resolves(foundCart);

        // WHEN
        comp.beforeRouteEnter({ params: { cartId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.cart).toBe(foundCart);
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
