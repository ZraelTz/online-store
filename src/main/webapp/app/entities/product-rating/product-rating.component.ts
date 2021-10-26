import { mixins } from 'vue-class-component';

import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { IProductRating } from '@/shared/model/product-rating.model';

import ProductRatingService from './product-rating.service';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class ProductRating extends Vue {
  @Inject('productRatingService') private productRatingService: () => ProductRatingService;
  private removeId: number = null;

  public productRatings: IProductRating[] = [];

  public isFetching = false;

  public mounted(): void {
    this.retrieveAllProductRatings();
  }

  public clear(): void {
    this.retrieveAllProductRatings();
  }

  public retrieveAllProductRatings(): void {
    this.isFetching = true;
    this.productRatingService()
      .retrieve()
      .then(
        res => {
          this.productRatings = res.data;
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

  public prepareRemove(instance: IProductRating): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeProductRating(): void {
    this.productRatingService()
      .delete(this.removeId)
      .then(() => {
        const message = this.$t('storeApp.productRating.deleted', { param: this.removeId });
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllProductRatings();
        this.closeDialog();
      });
  }

  public closeDialog(): void {
    (<any>this.$refs.removeEntity).hide();
  }
}
