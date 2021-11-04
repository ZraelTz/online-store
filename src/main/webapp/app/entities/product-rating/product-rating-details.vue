<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="productRating">
        <h2 class="jh-entity-heading" data-cy="productRatingDetailsHeading">
          <span v-text="$t('storeApp.productRating.detail.title')">ProductRating</span> {{ productRating.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('storeApp.productRating.rating')">Rating</span>
          </dt>
          <dd>
            <span>{{ productRating.rating }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.productRating.date')">Date</span>
          </dt>
          <dd>
            <span v-if="productRating.date">{{ $d(Date.parse(productRating.date), 'long') }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.productRating.user')">User</span>
          </dt>
          <dd>
            {{ productRating.user ? productRating.user.login : '' }}
          </dd>
          <dt>
            <span v-text="$t('storeApp.productRating.product')">Product</span>
          </dt>
          <dd>
            <div v-if="productRating.product">
              <router-link :to="{ name: 'ProductView', params: { productId: productRating.product.id } }">{{
                productRating.product.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link
          v-if="productRating.id"
          :to="{ name: 'ProductRatingEdit', params: { productRatingId: productRating.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./product-rating-details.component.ts"></script>
