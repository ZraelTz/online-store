import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, numeric } from 'vuelidate/lib/validators';

import UserService from '@/admin/user-management/user-management.service';

import { IVendor, Vendor } from '@/shared/model/vendor.model';
import VendorService from './vendor.service';

const validations: any = {
  vendor: {
    firstName: {
      required,
    },
    lastName: {
      required,
    },
    gender: {
      required,
    },
    email: {
      required,
    },
    phone: {
      required,
    },
    phone2: {},
    addressLine1: {
      required,
    },
    addressLine2: {},
    city: {
      required,
    },
    country: {
      required,
    },
    storeName: {
      required,
    },
    businessAccountNumber: {
      required,
      numeric,
    },
    user: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class VendorUpdate extends Vue {
  @Inject('vendorService') private vendorService: () => VendorService;
  public vendor: IVendor = new Vendor();

  @Inject('userService') private userService: () => UserService;

  public users: Array<any> = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.vendorId) {
        vm.retrieveVendor(to.params.vendorId);
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
    if (this.vendor.id) {
      this.vendorService()
        .update(this.vendor)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('storeApp.vendor.updated', { param: param.id });
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        });
    } else {
      this.vendorService()
        .create(this.vendor)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('storeApp.vendor.created', { param: param.id });
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

  public retrieveVendor(vendorId): void {
    this.vendorService()
      .find(vendorId)
      .then(res => {
        this.vendor = res;
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
  }
}
