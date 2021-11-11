import { Component, Vue, Inject } from 'vue-property-decorator';

import { ICartItem } from '@/shared/model/cart-item.model';
import CartItemService from './cart-item.service';

@Component
export default class CartItemDetails extends Vue {
  @Inject('cartItemService') private cartItemService: () => CartItemService;
  public cartItem: ICartItem = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.cartItemId) {
        vm.retrieveCartItem(to.params.cartItemId);
      }
    });
  }

  public retrieveCartItem(cartItemId) {
    this.cartItemService()
      .find(cartItemId)
      .then(res => {
        this.cartItem = res;
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
