/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import ShipmentDetailComponent from '@/entities/shipment/shipment-details.vue';
import ShipmentClass from '@/entities/shipment/shipment-details.component';
import ShipmentService from '@/entities/shipment/shipment.service';
import router from '@/router';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Shipment Management Detail Component', () => {
    let wrapper: Wrapper<ShipmentClass>;
    let comp: ShipmentClass;
    let shipmentServiceStub: SinonStubbedInstance<ShipmentService>;

    beforeEach(() => {
      shipmentServiceStub = sinon.createStubInstance<ShipmentService>(ShipmentService);

      wrapper = shallowMount<ShipmentClass>(ShipmentDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { shipmentService: () => shipmentServiceStub },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundShipment = { id: 123 };
        shipmentServiceStub.find.resolves(foundShipment);

        // WHEN
        comp.retrieveShipment(123);
        await comp.$nextTick();

        // THEN
        expect(comp.shipment).toBe(foundShipment);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundShipment = { id: 123 };
        shipmentServiceStub.find.resolves(foundShipment);

        // WHEN
        comp.beforeRouteEnter({ params: { shipmentId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.shipment).toBe(foundShipment);
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
