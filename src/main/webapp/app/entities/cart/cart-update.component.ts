import { Component, Vue, Inject } from 'vue-property-decorator';

import { required } from 'vuelidate/lib/validators';
import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '@/shared/date/filters';

import UserService from '@/admin/user-management/user-management.service';

import CheckoutService from '@/entities/checkout/checkout.service';
import { ICheckout } from '@/shared/model/checkout.model';

import CartItemService from '@/entities/cart-item/cart-item.service';
import { ICartItem } from '@/shared/model/cart-item.model';

import { ICart, Cart } from '@/shared/model/cart.model';
import CartService from './cart.service';

const validations: any = {
  cart: {
    date: {
      required,
    },
    user: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class CartUpdate extends Vue {
  @Inject('cartService') private cartService: () => CartService;
  public cart: ICart = new Cart();

  @Inject('userService') private userService: () => UserService;

  public users: Array<any> = [];

  @Inject('checkoutService') private checkoutService: () => CheckoutService;

  public checkouts: ICheckout[] = [];

  @Inject('cartItemService') private cartItemService: () => CartItemService;

  public cartItems: ICartItem[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.cartId) {
        vm.retrieveCart(to.params.cartId);
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
    if (this.cart.id) {
      this.cartService()
        .update(this.cart)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('storeApp.cart.updated', { param: param.id });
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        });
    } else {
      this.cartService()
        .create(this.cart)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('storeApp.cart.created', { param: param.id });
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
      this.cart[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.cart[field] = null;
    }
  }

  public updateZonedDateTimeField(field, event) {
    if (event.target.value) {
      this.cart[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.cart[field] = null;
    }
  }

  public retrieveCart(cartId): void {
    this.cartService()
      .find(cartId)
      .then(res => {
        res.date = new Date(res.date);
        this.cart = res;
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.userService()
      .retrieve()
      .then(res => {
        this.users = res.data;
      });
    this.checkoutService()
      .retrieve()
      .then(res => {
        this.checkouts = res.data;
      });
    this.cartItemService()
      .retrieve()
      .then(res => {
        this.cartItems = res.data;
      });
  }
}
