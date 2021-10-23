<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="shipment">
        <h2 class="jh-entity-heading" data-cy="shipmentDetailsHeading">
          <span v-text="$t('storeApp.shipment.detail.title')">Shipment</span> {{ shipment.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('storeApp.shipment.trackingCode')">Tracking Code</span>
          </dt>
          <dd>
            <span>{{ shipment.trackingCode }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.shipment.date')">Date</span>
          </dt>
          <dd>
            <span v-if="shipment.date">{{ $d(Date.parse(shipment.date), 'long') }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.shipment.details')">Details</span>
          </dt>
          <dd>
            <span>{{ shipment.details }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.shipment.invoice')">Invoice</span>
          </dt>
          <dd>
            <div v-if="shipment.invoice">
              <router-link :to="{ name: 'InvoiceView', params: { invoiceId: shipment.invoice.id } }">{{
                shipment.invoice.code
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link v-if="shipment.id" :to="{ name: 'ShipmentEdit', params: { shipmentId: shipment.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" v-if="hasAnyAuthority('ROLE_ADMIN')" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./shipment-details.component.ts"></script>
