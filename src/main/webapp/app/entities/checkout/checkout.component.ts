import { mixins } from 'vue-class-component';

import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { ICheckout } from '@/shared/model/checkout.model';

import CheckoutService from './checkout.service';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class Checkout extends Vue {
  @Inject('checkoutService') private checkoutService: () => CheckoutService;
  private removeId: number = null;

  public checkouts: ICheckout[] = [];

  public isFetching = false;

  public mounted(): void {
    this.retrieveAllCheckouts();
  }

  public clear(): void {
    this.retrieveAllCheckouts();
  }

  public retrieveAllCheckouts(): void {
    this.isFetching = true;
    this.checkoutService()
      .retrieve()
      .then(
        res => {
          this.checkouts = res.data;
          this.isFetching = false;
        },
        err => {
          this.isFetching = false;
        }
      );
  }

  public handleSyncList(): void {
    this.clear();
  }

  public prepareRemove(instance: ICheckout): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeCheckout(): void {
    this.checkoutService()
      .delete(this.removeId)
      .then(() => {
        const message = this.$t('storeApp.checkout.deleted', { param: this.removeId });
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllCheckouts();
        this.closeDialog();
      });
  }

  public closeDialog(): void {
    (<any>this.$refs.removeEntity).hide();
  }
}
