/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';

import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '@/shared/date/filters';

import * as config from '@/shared/config/config';
import ProductOrderUpdateComponent from '@/entities/product-order/product-order-update.vue';
import ProductOrderClass from '@/entities/product-order/product-order-update.component';
import ProductOrderService from '@/entities/product-order/product-order.service';

import InvoiceService from '@/entities/invoice/invoice.service';

import OrderedItemService from '@/entities/ordered-item/ordered-item.service';

import CustomerService from '@/entities/customer/customer.service';

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
  describe('ProductOrder Management Update Component', () => {
    let wrapper: Wrapper<ProductOrderClass>;
    let comp: ProductOrderClass;
    let productOrderServiceStub: SinonStubbedInstance<ProductOrderService>;

    beforeEach(() => {
      productOrderServiceStub = sinon.createStubInstance<ProductOrderService>(ProductOrderService);

      wrapper = shallowMount<ProductOrderClass>(ProductOrderUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          productOrderService: () => productOrderServiceStub,

          invoiceService: () => new InvoiceService(),

          orderedItemService: () => new OrderedItemService(),

          customerService: () => new CustomerService(),
        },
      });
      comp = wrapper.vm;
    });

    describe('load', () => {
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.productOrder = entity;
        productOrderServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(productOrderServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.productOrder = entity;
        productOrderServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(productOrderServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundProductOrder = { id: 123 };
        productOrderServiceStub.find.resolves(foundProductOrder);
        productOrderServiceStub.retrieve.resolves([foundProductOrder]);

        // WHEN
        comp.beforeRouteEnter({ params: { productOrderId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.productOrder).toBe(foundProductOrder);
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
