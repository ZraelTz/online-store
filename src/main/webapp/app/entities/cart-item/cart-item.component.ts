import { mixins } from 'vue-class-component';

import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { ICartItem } from '@/shared/model/cart-item.model';

import CartItemService from './cart-item.service';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class CartItem extends Vue {
  @Inject('cartItemService') private cartItemService: () => CartItemService;
  private removeId: number = null;

  public cartItems: ICartItem[] = [];

  public isFetching = false;

  public mounted(): void {
    this.retrieveAllCartItems();
  }

  public clear(): void {
    this.retrieveAllCartItems();
  }

  public retrieveAllCartItems(): void {
    this.isFetching = true;
    this.cartItemService()
      .retrieve()
      .then(
        res => {
          this.cartItems = res.data;
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

  public prepareRemove(instance: ICartItem): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeCartItem(): void {
    this.cartItemService()
      .delete(this.removeId)
      .then(() => {
        const message = this.$t('storeApp.cartItem.deleted', { param: this.removeId });
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllCartItems();
        this.closeDialog();
      });
  }

  public closeDialog(): void {
    (<any>this.$refs.removeEntity).hide();
  }
}
