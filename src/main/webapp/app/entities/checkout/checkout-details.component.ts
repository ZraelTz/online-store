import { Component, Vue, Inject } from 'vue-property-decorator';

import { ICheckout } from '@/shared/model/checkout.model';
import CheckoutService from './checkout.service';

@Component
export default class CheckoutDetails extends Vue {
  @Inject('checkoutService') private checkoutService: () => CheckoutService;
  public checkout: ICheckout = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.checkoutId) {
        vm.retrieveCheckout(to.params.checkoutId);
      }
    });
  }

  public retrieveCheckout(checkoutId) {
    this.checkoutService()
      .find(checkoutId)
      .then(res => {
        this.checkout = res;
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
