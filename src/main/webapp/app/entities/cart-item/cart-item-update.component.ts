import { Component, Vue, Inject } from 'vue-property-decorator';

import { numeric, required, minValue } from 'vuelidate/lib/validators';

import CartService from '@/entities/cart/cart.service';
import { ICart } from '@/shared/model/cart.model';

import ProductService from '@/entities/product/product.service';
import { IProduct } from '@/shared/model/product.model';

import { ICartItem, CartItem } from '@/shared/model/cart-item.model';
import CartItemService from './cart-item.service';

const validations: any = {
  cartItem: {
    quantity: {
      required,
      numeric,
      min: minValue(0),
    },
    product: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class CartItemUpdate extends Vue {
  @Inject('cartItemService') private cartItemService: () => CartItemService;
  public cartItem: ICartItem = new CartItem();

  @Inject('cartService') private cartService: () => CartService;

  public carts: ICart[] = [];

  @Inject('productService') private productService: () => ProductService;

  public products: IProduct[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.cartItemId) {
        vm.retrieveCartItem(to.params.cartItemId);
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
    if (this.cartItem.id) {
      this.cartItemService()
        .update(this.cartItem)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('storeApp.cartItem.updated', { param: param.id });
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        });
    } else {
      this.cartItemService()
        .create(this.cartItem)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('storeApp.cartItem.created', { param: param.id });
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

  public retrieveCartItem(cartItemId): void {
    this.cartItemService()
      .find(cartItemId)
      .then(res => {
        this.cartItem = res;
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.cartService()
      .retrieve()
      .then(res => {
        this.carts = res.data;
      });
    this.productService()
      .retrieve()
      .then(res => {
        this.products = res.data;
      });
  }
}
