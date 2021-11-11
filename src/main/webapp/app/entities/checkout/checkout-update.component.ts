import { Component, Vue, Inject } from 'vue-property-decorator';

import { numeric, required, minValue, decimal } from 'vuelidate/lib/validators';

import CartService from '@/entities/cart/cart.service';
import { ICart } from '@/shared/model/cart.model';

import { ICheckout, Checkout } from '@/shared/model/checkout.model';
import CheckoutService from './checkout.service';

const validations: any = {
  checkout: {
    quantity: {
      required,
      numeric,
      min: minValue(0),
    },
    totalPrice: {
      required,
      decimal,
      min: minValue(0),
    },
    cart: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class CheckoutUpdate extends Vue {
  @Inject('checkoutService') private checkoutService: () => CheckoutService;
  public checkout: ICheckout = new Checkout();

  @Inject('cartService') private cartService: () => CartService;

  public carts: ICart[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.checkoutId) {
        vm.retrieveCheckout(to.params.checkoutId);
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
    if (this.checkout.id) {
      this.checkoutService()
        .update(this.checkout)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('storeApp.checkout.updated', { param: param.id });
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        });
    } else {
      this.checkoutService()
        .create(this.checkout)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('storeApp.checkout.created', { param: param.id });
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

  public retrieveCheckout(checkoutId): void {
    this.checkoutService()
      .find(checkoutId)
      .then(res => {
        this.checkout = res;
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.cartService()
      .retrieve()
      .then(res => {
        this.carts = res.data;
      });
  }
}
