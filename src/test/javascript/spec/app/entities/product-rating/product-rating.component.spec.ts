/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';

import * as config from '@/shared/config/config';
import ProductRatingComponent from '@/entities/product-rating/product-rating.vue';
import ProductRatingClass from '@/entities/product-rating/product-rating.component';
import ProductRatingService from '@/entities/product-rating/product-rating.service';

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
  describe('ProductRating Management Component', () => {
    let wrapper: Wrapper<ProductRatingClass>;
    let comp: ProductRatingClass;
    let productRatingServiceStub: SinonStubbedInstance<ProductRatingService>;

    beforeEach(() => {
      productRatingServiceStub = sinon.createStubInstance<ProductRatingService>(ProductRatingService);
      productRatingServiceStub.retrieve.resolves({ headers: {} });

      wrapper = shallowMount<ProductRatingClass>(ProductRatingComponent, {
        store,
        i18n,
        localVue,
        stubs: { bModal: bModalStub as any },
        provide: {
          productRatingService: () => productRatingServiceStub,
        },
      });
      comp = wrapper.vm;
    });

    it('Should call load all on init', async () => {
      // GIVEN
      productRatingServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

      // WHEN
      comp.retrieveAllProductRatings();
      await comp.$nextTick();

      // THEN
      expect(productRatingServiceStub.retrieve.called).toBeTruthy();
      expect(comp.productRatings[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
    it('Should call delete service on confirmDelete', async () => {
      // GIVEN
      productRatingServiceStub.delete.resolves({});

      // WHEN
      comp.prepareRemove({ id: 123 });
      comp.removeProductRating();
      await comp.$nextTick();

      // THEN
      expect(productRatingServiceStub.delete.called).toBeTruthy();
      expect(productRatingServiceStub.retrieve.callCount).toEqual(1);
    });
  });
});
