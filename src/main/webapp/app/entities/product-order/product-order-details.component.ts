import { Component, Vue, Inject } from 'vue-property-decorator';

import { IProductOrder } from '@/shared/model/product-order.model';
import ProductOrderService from './product-order.service';
import AccountService from '@/account/account.service';

@Component
export default class ProductOrderDetails extends Vue {
  @Inject('accountService') private accountService: () => AccountService;
  @Inject('productOrderService') private productOrderService: () => ProductOrderService;
  public productOrder: IProductOrder = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.productOrderId) {
        vm.retrieveProductOrder(to.params.productOrderId);
      }
    });
  }

  public retrieveProductOrder(productOrderId) {
    this.productOrderService()
      .find(productOrderId)
      .then(res => {
        this.productOrder = res;
      });
  }

  public hasAnyAuthority(auhtorities: any): boolean {
    return this.accountService().hasAnyAuthority(auhtorities);
  }

  public previousState() {
    this.$router.go(-1);
  }
}
