import { Component, Vue, Inject } from 'vue-property-decorator';

import { decimal, required, minValue, maxValue } from 'vuelidate/lib/validators';
import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '@/shared/date/filters';

import UserService from '@/admin/user-management/user-management.service';

import ProductService from '@/entities/product/product.service';
import { IProduct } from '@/shared/model/product.model';

import { IProductRating, ProductRating } from '@/shared/model/product-rating.model';
import ProductRatingService from './product-rating.service';

const validations: any = {
  productRating: {
    rating: {
      required,
      decimal,
      min: minValue(0),
      max: maxValue(5),
    },
    date: {
      required,
    },
    user: {
      required,
    },
    product: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class ProductRatingUpdate extends Vue {
  @Inject('productRatingService') private productRatingService: () => ProductRatingService;
  public productRating: IProductRating = new ProductRating();

  @Inject('userService') private userService: () => UserService;

  public users: Array<any> = [];

  @Inject('productService') private productService: () => ProductService;

  public products: IProduct[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.productRatingId) {
        vm.retrieveProductRating(to.params.productRatingId);
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
    if (this.productRating.id) {
      this.productRatingService()
        .update(this.productRating)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('storeApp.productRating.updated', { param: param.id });
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        });
    } else {
      this.productRatingService()
        .create(this.productRating)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('storeApp.productRating.created', { param: param.id });
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
      this.productRating[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.productRating[field] = null;
    }
  }

  public updateZonedDateTimeField(field, event) {
    if (event.target.value) {
      this.productRating[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.productRating[field] = null;
    }
  }

  public retrieveProductRating(productRatingId): void {
    this.productRatingService()
      .find(productRatingId)
      .then(res => {
        res.date = new Date(res.date);
        this.productRating = res;
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
    this.productService()
      .retrieve()
      .then(res => {
        this.products = res.data;
      });
  }
}
