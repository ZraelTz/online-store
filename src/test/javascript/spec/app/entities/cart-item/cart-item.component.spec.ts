/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';

import * as config from '@/shared/config/config';
import CartItemComponent from '@/entities/cart-item/cart-item.vue';
import CartItemClass from '@/entities/cart-item/cart-item.component';
import CartItemService from '@/entities/cart-item/cart-item.service';

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
  describe('CartItem Management Component', () => {
    let wrapper: Wrapper<CartItemClass>;
    let comp: CartItemClass;
    let cartItemServiceStub: SinonStubbedInstance<CartItemService>;

    beforeEach(() => {
      cartItemServiceStub = sinon.createStubInstance<CartItemService>(CartItemService);
      cartItemServiceStub.retrieve.resolves({ headers: {} });

      wrapper = shallowMount<CartItemClass>(CartItemComponent, {
        store,
        i18n,
        localVue,
        stubs: { bModal: bModalStub as any },
        provide: {
          cartItemService: () => cartItemServiceStub,
        },
      });
      comp = wrapper.vm;
    });

    it('Should call load all on init', async () => {
      // GIVEN
      cartItemServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

      // WHEN
      comp.retrieveAllCartItems();
      await comp.$nextTick();

      // THEN
      expect(cartItemServiceStub.retrieve.called).toBeTruthy();
      expect(comp.cartItems[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
    it('Should call delete service on confirmDelete', async () => {
      // GIVEN
      cartItemServiceStub.delete.resolves({});

      // WHEN
      comp.prepareRemove({ id: 123 });
      comp.removeCartItem();
      await comp.$nextTick();

      // THEN
      expect(cartItemServiceStub.delete.called).toBeTruthy();
      expect(cartItemServiceStub.retrieve.callCount).toEqual(1);
    });
  });
});