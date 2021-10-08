/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';

import * as config from '@/shared/config/config';
import OrderedItemUpdateComponent from '@/entities/ordered-item/ordered-item-update.vue';
import OrderedItemClass from '@/entities/ordered-item/ordered-item-update.component';
import OrderedItemService from '@/entities/ordered-item/ordered-item.service';

import ProductService from '@/entities/product/product.service';

import ProductOrderService from '@/entities/product-order/product-order.service';

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
  describe('OrderedItem Management Update Component', () => {
    let wrapper: Wrapper<OrderedItemClass>;
    let comp: OrderedItemClass;
    let orderedItemServiceStub: SinonStubbedInstance<OrderedItemService>;

    beforeEach(() => {
      orderedItemServiceStub = sinon.createStubInstance<OrderedItemService>(OrderedItemService);

      wrapper = shallowMount<OrderedItemClass>(OrderedItemUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          orderedItemService: () => orderedItemServiceStub,

          productService: () => new ProductService(),

          productOrderService: () => new ProductOrderService(),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.orderedItem = entity;
        orderedItemServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(orderedItemServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.orderedItem = entity;
        orderedItemServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(orderedItemServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundOrderedItem = { id: 123 };
        orderedItemServiceStub.find.resolves(foundOrderedItem);
        orderedItemServiceStub.retrieve.resolves([foundOrderedItem]);

        // WHEN
        comp.beforeRouteEnter({ params: { orderedItemId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.orderedItem).toBe(foundOrderedItem);
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
