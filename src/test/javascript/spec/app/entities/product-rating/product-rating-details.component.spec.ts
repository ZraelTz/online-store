/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import ProductRatingDetailComponent from '@/entities/product-rating/product-rating-details.vue';
import ProductRatingClass from '@/entities/product-rating/product-rating-details.component';
import ProductRatingService from '@/entities/product-rating/product-rating.service';
import router from '@/router';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('ProductRating Management Detail Component', () => {
    let wrapper: Wrapper<ProductRatingClass>;
    let comp: ProductRatingClass;
    let productRatingServiceStub: SinonStubbedInstance<ProductRatingService>;

    beforeEach(() => {
      productRatingServiceStub = sinon.createStubInstance<ProductRatingService>(ProductRatingService);

      wrapper = shallowMount<ProductRatingClass>(ProductRatingDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { productRatingService: () => productRatingServiceStub },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundProductRating = { id: 123 };
        productRatingServiceStub.find.resolves(foundProductRating);

        // WHEN
        comp.retrieveProductRating(123);
        await comp.$nextTick();

        // THEN
        expect(comp.productRating).toBe(foundProductRating);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundProductRating = { id: 123 };
        productRatingServiceStub.find.resolves(foundProductRating);

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
