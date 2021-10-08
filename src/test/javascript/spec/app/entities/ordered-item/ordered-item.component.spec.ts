/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';

import * as config from '@/shared/config/config';
import OrderedItemComponent from '@/entities/ordered-item/ordered-item.vue';
import OrderedItemClass from '@/entities/ordered-item/ordered-item.component';
import OrderedItemService from '@/entities/ordered-item/ordered-item.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('b-badge', {});
localVue.component('jhi-sort-indicator', {});
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
  describe('OrderedItem Management Component', () => {
    let wrapper: Wrapper<OrderedItemClass>;
    let comp: OrderedItemClass;
    let orderedItemServiceStub: SinonStubbedInstance<OrderedItemService>;

    beforeEach(() => {
      orderedItemServiceStub = sinon.createStubInstance<OrderedItemService>(OrderedItemService);
      orderedItemServiceStub.retrieve.resolves({ headers: {} });

      wrapper = shallowMount<OrderedItemClass>(OrderedItemComponent, {
        store,
        i18n,
        localVue,
        stubs: { jhiItemCount: true, bPagination: true, bModal: bModalStub as any },
        provide: {
          orderedItemService: () => orderedItemServiceStub,
        },
      });
      comp = wrapper.vm;
    });

    it('Should call load all on init', async () => {
      // GIVEN
      orderedItemServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

      // WHEN
      comp.retrieveAllOrderedItems();
      await comp.$nextTick();

      // THEN
      expect(orderedItemServiceStub.retrieve.called).toBeTruthy();
      expect(comp.orderedItems[0]).toEqual(expect.objectContaining({ id: 123 }));
    });

    it('should load a page', async () => {
      // GIVEN
      orderedItemServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });
      comp.previousPage = 1;

      // WHEN
      comp.loadPage(2);
      await comp.$nextTick();

      // THEN
      expect(orderedItemServiceStub.retrieve.called).toBeTruthy();
      expect(comp.orderedItems[0]).toEqual(expect.objectContaining({ id: 123 }));
    });

    it('should not load a page if the page is the same as the previous page', () => {
      // GIVEN
      orderedItemServiceStub.retrieve.reset();
      comp.previousPage = 1;

      // WHEN
      comp.loadPage(1);

      // THEN
      expect(orderedItemServiceStub.retrieve.called).toBeFalsy();
    });

    it('should re-initialize the page', async () => {
      // GIVEN
      orderedItemServiceStub.retrieve.reset();
      orderedItemServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

      // WHEN
      comp.loadPage(2);
      await comp.$nextTick();
      comp.clear();
      await comp.$nextTick();

      // THEN
      expect(orderedItemServiceStub.retrieve.callCount).toEqual(3);
      expect(comp.page).toEqual(1);
      expect(comp.orderedItems[0]).toEqual(expect.objectContaining({ id: 123 }));
    });

    it('should calculate the sort attribute for an id', () => {
      // WHEN
      const result = comp.sort();

      // THEN
      expect(result).toEqual(['id,asc']);
    });

    it('should calculate the sort attribute for a non-id attribute', () => {
      // GIVEN
      comp.propOrder = 'name';

      // WHEN
      const result = comp.sort();

      // THEN
      expect(result).toEqual(['name,asc', 'id']);
    });
    it('Should call delete service on confirmDelete', async () => {
      // GIVEN
      orderedItemServiceStub.delete.resolves({});

      // WHEN
      comp.prepareRemove({ id: 123 });
      comp.removeOrderedItem();
      await comp.$nextTick();

      // THEN
      expect(orderedItemServiceStub.delete.called).toBeTruthy();
      expect(orderedItemServiceStub.retrieve.callCount).toEqual(1);
    });
  });
});
