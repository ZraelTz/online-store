import { mixins } from 'vue-class-component';

import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { IOrderedItem } from '@/shared/model/ordered-item.model';

import OrderedItemService from './ordered-item.service';
import AccountService from '@/account/account.service';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class OrderedItem extends Vue {
  @Inject('accountService') private accountService: () => AccountService;
  @Inject('orderedItemService') private orderedItemService: () => OrderedItemService;
  private removeId: number = null;
  public itemsPerPage = 20;
  public queryCount: number = null;
  public page = 1;
  public previousPage = 1;
  public propOrder = 'id';
  public reverse = false;
  public totalItems = 0;

  public orderedItems: IOrderedItem[] = [];

  public isFetching = false;

  public mounted(): void {
    this.retrieveAllOrderedItems();
  }

  public clear(): void {
    this.page = 1;
    this.retrieveAllOrderedItems();
  }

  public hasAnyAuthority(auhtorities: any): boolean {
    return this.accountService().hasAnyAuthority(auhtorities);
  }

  public retrieveAllOrderedItems(): void {
    this.isFetching = true;
    const paginationQuery = {
      page: this.page - 1,
      size: this.itemsPerPage,
      sort: this.sort(),
    };
    this.orderedItemService()
      .retrieve(paginationQuery)
      .then(
        res => {
          this.orderedItems = res.data;
          this.totalItems = Number(res.headers['x-total-count']);
          this.queryCount = this.totalItems;
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

  public prepareRemove(instance: IOrderedItem): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeOrderedItem(): void {
    this.orderedItemService()
      .delete(this.removeId)
      .then(() => {
        const message = this.$t('storeApp.orderedItem.deleted', { param: this.removeId });
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllOrderedItems();
        this.closeDialog();
      });
  }

  public sort(): Array<any> {
    const result = [this.propOrder + ',' + (this.reverse ? 'desc' : 'asc')];
    if (this.propOrder !== 'id') {
      result.push('id');
    }
    return result;
  }

  public loadPage(page: number): void {
    if (page !== this.previousPage) {
      this.previousPage = page;
      this.transition();
    }
  }

  public transition(): void {
    this.retrieveAllOrderedItems();
  }

  public changeOrder(propOrder): void {
    this.propOrder = propOrder;
    this.reverse = !this.reverse;
    this.transition();
  }

  public closeDialog(): void {
    (<any>this.$refs.removeEntity).hide();
  }
}
