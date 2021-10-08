import { Component, Vue, Inject } from 'vue-property-decorator';

import { IShipment } from '@/shared/model/shipment.model';
import ShipmentService from './shipment.service';
import AccountService from '@/account/account.service';

@Component
export default class ShipmentDetails extends Vue {
  @Inject('accountService') private accountService: () => AccountService;
  @Inject('shipmentService') private shipmentService: () => ShipmentService;
  public shipment: IShipment = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.shipmentId) {
        vm.retrieveShipment(to.params.shipmentId);
      }
    });
  }

  public hasAnyAuthority(auhtorities: any): boolean {
    return this.accountService().hasAnyAuthority(auhtorities);
  }

  public retrieveShipment(shipmentId) {
    this.shipmentService()
      .find(shipmentId)
      .then(res => {
        this.shipment = res;
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
