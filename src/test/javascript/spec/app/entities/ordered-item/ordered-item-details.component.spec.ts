/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import OrderedItemDetailComponent from '@/entities/ordered-item/ordered-item-details.vue';
import OrderedItemClass from '@/entities/ordered-item/ordered-item-details.component';
import OrderedItemService from '@/entities/ordered-item/ordered-item.service';
import router from '@/router';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('OrderedItem Management Detail Component', () => {
    let wrapper: Wrapper<OrderedItemClass>;
    let comp: OrderedItemClass;
    let orderedItemServiceStub: SinonStubbedInstance<OrderedItemService>;

    beforeEach(() => {
      orderedItemServiceStub = sinon.createStubInstance<OrderedItemService>(OrderedItemService);

      wrapper = shallowMount<OrderedItemClass>(OrderedItemDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { orderedItemService: () => orderedItemServiceStub },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundOrderedItem = { id: 123 };
        orderedItemServiceStub.find.resolves(foundOrderedItem);

        // WHEN
        comp.retrieveOrderedItem(123);
        await comp.$nextTick();

        // THEN
        expect(comp.orderedItem).toBe(foundOrderedItem);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundOrderedItem = { id: 123 };
        orderedItemServiceStub.find.resolves(foundOrderedItem);

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
