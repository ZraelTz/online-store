import { Component, Vue, Inject } from 'vue-property-decorator';

import { ICart } from '@/shared/model/cart.model';
import CartService from './cart.service';

@Component
export default class CartDetails extends Vue {
  @Inject('cartService') private cartService: () => CartService;
  public cart: ICart = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.cartId) {
        vm.retrieveCart(to.params.cartId);
      }
    });
  }

  public retrieveCart(cartId) {
    this.cartService()
      .find(cartId)
      .then(res => {
        this.cart = res;
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
