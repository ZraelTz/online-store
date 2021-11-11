/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';

import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '@/shared/date/filters';

import * as config from '@/shared/config/config';
import ProductRatingUpdateComponent from '@/entities/product-rating/product-rating-update.vue';
import ProductRatingClass from '@/entities/product-rating/product-rating-update.component';
import ProductRatingService from '@/entities/product-rating/product-rating.service';

import UserService from '@/admin/user-management/user-management.service';

import ProductService from '@/entities/product/product.service';

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
  describe('ProductRating Management Update Component', () => {
    let wrapper: Wrapper<ProductRatingClass>;
    let comp: ProductRatingClass;
    let productRatingServiceStub: SinonStubbedInstance<ProductRatingService>;

    beforeEach(() => {
      productRatingServiceStub = sinon.createStubInstance<ProductRatingService>(ProductRatingService);

      wrapper = shallowMount<ProductRatingClass>(ProductRatingUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          productRatingService: () => productRatingServiceStub,

          userService: () => new UserService(),

          productService: () => new ProductService(),
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
        comp.productRating = entity;
        productRatingServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(productRatingServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.productRating = entity;
        productRatingServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(productRatingServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundProductRating = { id: 123 };
        productRatingServiceStub.find.resolves(foundProductRating);
        productRatingServiceStub.retrieve.resolves([foundProductRating]);

        // WHEN
        comp.beforeRouteEnter({ params: { productRatingId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.productRating).toBe(foundProductRating);
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
