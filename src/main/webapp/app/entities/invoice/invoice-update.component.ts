import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, decimal } from 'vuelidate/lib/validators';
import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '@/shared/date/filters';

import ShipmentService from '@/entities/shipment/shipment.service';
import { IShipment } from '@/shared/model/shipment.model';

import ProductOrderService from '@/entities/product-order/product-order.service';
import { IProductOrder } from '@/shared/model/product-order.model';

import { IInvoice, Invoice } from '@/shared/model/invoice.model';
import InvoiceService from './invoice.service';

const validations: any = {
  invoice: {
    date: {
      required,
    },
    details: {},
    status: {
      required,
    },
    paymentMethod: {
      required,
    },
    paymentDate: {
      required,
    },
    paymentAmount: {
      required,
      decimal,
    },
    code: {},
    order: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class InvoiceUpdate extends Vue {
  @Inject('invoiceService') private invoiceService: () => InvoiceService;
  public invoice: IInvoice = new Invoice();

  @Inject('shipmentService') private shipmentService: () => ShipmentService;

  public shipments: IShipment[] = [];

  @Inject('productOrderService') private productOrderService: () => ProductOrderService;

  public productOrders: IProductOrder[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.invoiceId) {
        vm.retrieveInvoice(to.params.invoiceId);
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
    if (this.invoice.id) {
      this.invoiceService()
        .update(this.invoice)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('storeApp.invoice.updated', { param: param.id });
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        });
    } else {
      this.invoiceService()
        .create(this.invoice)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('storeApp.invoice.created', { param: param.id });
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
      this.invoice[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.invoice[field] = null;
    }
  }

  public updateZonedDateTimeField(field, event) {
    if (event.target.value) {
      this.invoice[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.invoice[field] = null;
    }
  }

  public retrieveInvoice(invoiceId): void {
    this.invoiceService()
      .find(invoiceId)
      .then(res => {
        res.date = new Date(res.date);
        res.paymentDate = new Date(res.paymentDate);
        this.invoice = res;
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.shipmentService()
      .retrieve()
      .then(res => {
        this.shipments = res.data;
      });
    this.productOrderService()
      .retrieve()
      .then(res => {
        this.productOrders = res.data;
      });
  }
}
