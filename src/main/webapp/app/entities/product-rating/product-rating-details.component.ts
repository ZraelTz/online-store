import { Component, Vue, Inject } from 'vue-property-decorator';

import { IProductRating } from '@/shared/model/product-rating.model';
import ProductRatingService from './product-rating.service';

@Component
export default class ProductRatingDetails extends Vue {
  @Inject('productRatingService') private productRatingService: () => ProductRatingService;
  public productRating: IProductRating = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.productRatingId) {
        vm.retrieveProductRating(to.params.productRatingId);
      }
    });
  }

  public retrieveProductRating(productRatingId) {
    this.productRatingService()
      .find(productRatingId)
      .then(res => {
        this.productRating = res;
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
