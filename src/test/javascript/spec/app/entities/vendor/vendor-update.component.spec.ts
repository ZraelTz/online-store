/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';

import * as config from '@/shared/config/config';
import VendorUpdateComponent from '@/entities/vendor/vendor-update.vue';
import VendorClass from '@/entities/vendor/vendor-update.component';
import VendorService from '@/entities/vendor/vendor.service';

import UserService from '@/admin/user-management/user-management.service';

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
  describe('Vendor Management Update Component', () => {
    let wrapper: Wrapper<VendorClass>;
    let comp: VendorClass;
    let vendorServiceStub: SinonStubbedInstance<VendorService>;

    beforeEach(() => {
      vendorServiceStub = sinon.createStubInstance<VendorService>(VendorService);

      wrapper = shallowMount<VendorClass>(VendorUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          vendorService: () => vendorServiceStub,

          userService: () => new UserService(),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.vendor = entity;
        vendorServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(vendorServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.vendor = entity;
        vendorServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(vendorServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundVendor = { id: 123 };
        vendorServiceStub.find.resolves(foundVendor);
        vendorServiceStub.retrieve.resolves([foundVendor]);

        // WHEN
        comp.beforeRouteEnter({ params: { vendorId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.vendor).toBe(foundVendor);
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
