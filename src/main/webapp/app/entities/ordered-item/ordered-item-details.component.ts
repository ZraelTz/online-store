import { Component, Vue, Inject } from 'vue-property-decorator';

import { IOrderedItem } from '@/shared/model/ordered-item.model';
import OrderedItemService from './ordered-item.service';
import AccountService from '@/account/account.service';

@Component
export default class OrderedItemDetails extends Vue {
  @Inject('accountService') private accountService: () => AccountService;
  @Inject('orderedItemService') private orderedItemService: () => OrderedItemService;
  public orderedItem: IOrderedItem = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.orderedItemId) {
        vm.retrieveOrderedItem(to.params.orderedItemId);
      }
    });
  }

  public hasAnyAuthority(auhtorities: any): boolean {
    return this.accountService().hasAnyAuthority(auhtorities);
  }

  public retrieveOrderedItem(orderedItemId) {
    this.orderedItemService()
      .find(orderedItemId)
      .then(res => {
        this.orderedItem = res;
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
