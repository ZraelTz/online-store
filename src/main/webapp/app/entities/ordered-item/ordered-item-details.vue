<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="orderedItem">
        <h2 class="jh-entity-heading" data-cy="orderedItemDetailsHeading">
          <span v-text="$t('storeApp.orderedItem.detail.title')">OrderedItem</span> {{ orderedItem.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('storeApp.orderedItem.quantity')">Quantity</span>
          </dt>
          <dd>
            <span>{{ orderedItem.quantity }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.orderedItem.totalPrice')">Total Price</span>
          </dt>
          <dd>
            <span>{{ orderedItem.totalPrice }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.orderedItem.status')">Status</span>
          </dt>
          <dd>
            <span v-text="$t('storeApp.OrderedItemStatus.' + orderedItem.status)">{{ orderedItem.status }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.orderedItem.product')">Product</span>
          </dt>
          <dd>
            <div v-if="orderedItem.product">
              <router-link :to="{ name: 'ProductView', params: { productId: orderedItem.product.id } }">{{
                orderedItem.product.name
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="$t('storeApp.orderedItem.order')">Order</span>
          </dt>
          <dd>
            <div v-if="orderedItem.order">
              <router-link :to="{ name: 'ProductOrderView', params: { productOrderId: orderedItem.order.id } }">{{
                orderedItem.order.code
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link
          v-if="orderedItem.id"
          :to="{ name: 'OrderedItemEdit', params: { orderedItemId: orderedItem.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" v-if="hasAnyAuthority('ROLE_ADMIN')" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./ordered-item-details.component.ts"></script>
