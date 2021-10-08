import { Component, Vue, Inject } from 'vue-property-decorator';

import { required } from 'vuelidate/lib/validators';
import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '@/shared/date/filters';

import InvoiceService from '@/entities/invoice/invoice.service';
import { IInvoice } from '@/shared/model/invoice.model';

import OrderedItemService from '@/entities/ordered-item/ordered-item.service';
import { IOrderedItem } from '@/shared/model/ordered-item.model';

import CustomerService from '@/entities/customer/customer.service';
import { ICustomer } from '@/shared/model/customer.model';

import { IProductOrder, ProductOrder } from '@/shared/model/product-order.model';
import ProductOrderService from './product-order.service';

const validations: any = {
  productOrder: {
    placedDate: {
      required,
    },
    status: {
      required,
    },
    code: {
      required,
    },
    customer: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class ProductOrderUpdate extends Vue {
  @Inject('productOrderService') private productOrderService: () => ProductOrderService;
  public productOrder: IProductOrder = new ProductOrder();

  @Inject('invoiceService') private invoiceService: () => InvoiceService;

  public invoices: IInvoice[] = [];

  @Inject('orderedItemService') private orderedItemService: () => OrderedItemService;

  public orderedItems: IOrderedItem[] = [];

  @Inject('customerService') private customerService: () => CustomerService;

  public customers: ICustomer[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.productOrderId) {
        vm.retrieveProductOrder(to.params.productOrderId);
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
    if (this.productOrder.id) {
      this.productOrderService()
        .update(this.productOrder)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('storeApp.productOrder.updated', { param: param.id });
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        });
    } else {
      this.productOrderService()
        .create(this.productOrder)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('storeApp.productOrder.created', { param: param.id });
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
      this.productOrder[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.productOrder[field] = null;
    }
  }

  public updateZonedDateTimeField(field, event) {
    if (event.target.value) {
      this.productOrder[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.productOrder[field] = null;
    }
  }

  public retrieveProductOrder(productOrderId): void {
    this.productOrderService()
      .find(productOrderId)
      .then(res => {
        res.placedDate = new Date(res.placedDate);
        this.productOrder = res;
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
    this.orderedItemService()
      .retrieve()
      .then(res => {
        this.orderedItems = res.data;
      });
    this.customerService()
      .retrieve()
      .then(res => {
        this.customers = res.data;
      });
  }
}
