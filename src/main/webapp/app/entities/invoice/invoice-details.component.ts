import { Component, Vue, Inject } from 'vue-property-decorator';

import { IInvoice } from '@/shared/model/invoice.model';
import InvoiceService from './invoice.service';
import AccountService from '@/account/account.service';

@Component
export default class InvoiceDetails extends Vue {
  @Inject('accountService') private accountService: () => AccountService;
  @Inject('invoiceService') private invoiceService: () => InvoiceService;
  public invoice: IInvoice = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.invoiceId) {
        vm.retrieveInvoice(to.params.invoiceId);
      }
    });
  }

  public hasAnyAuthority(auhtorities: any): boolean {
    return this.accountService().hasAnyAuthority(auhtorities);
  }

  public retrieveInvoice(invoiceId) {
    this.invoiceService()
      .find(invoiceId)
      .then(res => {
        this.invoice = res;
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
