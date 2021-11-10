import { Component, Inject } from 'vue-property-decorator';

import { mixins } from 'vue-class-component';
import JhiDataUtils from '@/shared/data/data-utils.service';

import { required, decimal, minValue } from 'vuelidate/lib/validators';

import ProductCategoryService from '@/entities/product-category/product-category.service';
import { IProductCategory } from '@/shared/model/product-category.model';

import ProductRatingService from '@/entities/product-rating/product-rating.service';
import { IProductRating } from '@/shared/model/product-rating.model';

import { IProduct, Product } from '@/shared/model/product.model';
import ProductService from './product.service';

const validations: any = {
  product: {
    name: {
      required,
    },
    material: {},
    description: {},
    price: {
      required,
      decimal,
      min: minValue(0),
    },
    productSize: {
      required,
    },
    image: {},
    productCategory: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class ProductUpdate extends mixins(JhiDataUtils) {
  @Inject('productService') private productService: () => ProductService;
  public product: IProduct = new Product();

  @Inject('productCategoryService') private productCategoryService: () => ProductCategoryService;

  public productCategories: IProductCategory[] = [];

  @Inject('productRatingService') private productRatingService: () => ProductRatingService;

  public productRatings: IProductRating[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.productId) {
        vm.retrieveProduct(to.params.productId);
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
    if (this.product.id) {
      this.productService()
        .update(this.product)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('storeApp.product.updated', { param: param.id });
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        });
    } else {
      this.productService()
        .create(this.product)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('storeApp.product.created', { param: param.id });
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

  public retrieveProduct(productId): void {
    this.productService()
      .find(productId)
      .then(res => {
        this.product = res;
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public clearInputImage(field, fieldContentType, idInput): void {
    if (this.product && field && fieldContentType) {
      if (Object.prototype.hasOwnProperty.call(this.product, field)) {
        this.product[field] = null;
      }
      if (Object.prototype.hasOwnProperty.call(this.product, fieldContentType)) {
        this.product[fieldContentType] = null;
      }
      if (idInput) {
        (<any>this).$refs[idInput] = null;
      }
    }
  }

  public initRelationships(): void {
    this.productCategoryService()
      .retrieve()
      .then(res => {
        this.productCategories = res.data;
      });
    this.productRatingService()
      .retrieve()
      .then(res => {
        this.productRatings = res.data;
      });
  }
}
