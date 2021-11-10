<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="checkout">
        <h2 class="jh-entity-heading" data-cy="checkoutDetailsHeading">
          <span v-text="$t('storeApp.checkout.detail.title')">Checkout</span> {{ checkout.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('storeApp.checkout.quantity')">Quantity</span>
          </dt>
          <dd>
            <span>{{ checkout.quantity }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.checkout.totalPrice')">Total Price</span>
          </dt>
          <dd>
            <span>{{ checkout.totalPrice }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link v-if="checkout.id" :to="{ name: 'CheckoutEdit', params: { checkoutId: checkout.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./checkout-details.component.ts"></script>
