<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="productRating">
        <h2 class="jh-entity-heading" data-cy="productRatingDetailsHeading">
          <span v-text="$t('storeApp.productRating.detail.title')">ProductRating</span> {{ productRating.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('storeApp.productRating.value')">Value</span>
          </dt>
          <dd>
            <span>{{ productRating.value }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.productRating.productId')">Product Id</span>
          </dt>
          <dd>
            <span>{{ productRating.productId }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.productRating.userId')">User Id</span>
          </dt>
          <dd>
            <span>{{ productRating.userId }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.productRating.productRating')">Product Rating</span>
          </dt>
          <dd>
            <div v-if="productRating.productRating">
              <router-link :to="{ name: 'ProductView', params: { productId: productRating.productRating.id } }">{{
                productRating.productRating.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="$t('storeApp.productRating.rating')">Rating</span>
          </dt>
          <dd>
            {{ productRating.rating ? productRating.rating.login : '' }}
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
