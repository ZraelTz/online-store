import { Component, Vue, Inject } from 'vue-property-decorator';

import { required } from 'vuelidate/lib/validators';
import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '@/shared/date/filters';

import InvoiceService from '@/entities/invoice/invoice.service';
import { IInvoice } from '@/shared/model/invoice.model';

import { IShipment, Shipment } from '@/shared/model/shipment.model';
import ShipmentService from './shipment.service';

const validations: any = {
  shipment: {
    trackingCode: {},
    date: {
      required,
    },
    details: {},
    invoice: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class ShipmentUpdate extends Vue {
  @Inject('shipmentService') private shipmentService: () => ShipmentService;
  public shipment: IShipment = new Shipment();

  @Inject('invoiceService') private invoiceService: () => InvoiceService;

  public invoices: IInvoice[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.shipmentId) {
        vm.retrieveShipment(to.params.shipmentId);
      }
      vm.initRelationships();
    });
  }

  created(): void {
    this.currentLanguage = this.$store.getters.currentLanguage;
    this.$store.watch(
      () => this.$store.getters.currentLanguage,
      () => {
        this.currentLanguage = this.$store.getters.currentLanguage;
      }
    );
  }

  public save(): void {
    this.isSaving = true;
    if (this.shipment.id) {
      this.shipmentService()
        .update(this.shipment)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('storeApp.shipment.updated', { param: param.id });
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        });
    } else {
      this.shipmentService()
        .create(this.shipment)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('storeApp.shipment.created', { param: param.id });
          this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Success',
            variant: 'success',
            solid: true,
            autoHideDelay: 5000,
          });
        });
    }
  }

  public convertDateTimeFromServer(date: Date): string {
    if (date && dayjs(date).isValid()) {
      return dayjs(date).format(DATE_TIME_LONG_FORMAT);
    }
    return null;
  }

  public updateInstantField(field, event) {
    if (event.target.value) {
      this.shipment[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.shipment[field] = null;
    }
  }

  public updateZonedDateTimeField(field, event) {
    if (event.target.value) {
      this.shipment[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.shipment[field] = null;
    }
  }

  public retrieveShipment(shipmentId): void {
    this.shipmentService()
      .find(shipmentId)
      .then(res => {
        res.date = new Date(res.date);
        this.shipment = res;
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.invoiceService()
      .retrieve()
      .then(res => {
        this.invoices = res.data;
      });
  }
}
