<template>
  <div>
    <h2 id="page-heading" data-cy="ProductRatingHeading">
      <span v-text="$t('storeApp.productRating.home.title')" id="product-rating-heading">Product Ratings</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="$t('storeApp.productRating.home.refreshListLabel')">Refresh List</span>
        </button>
        <router-link :to="{ name: 'ProductRatingCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-product-rating"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="$t('storeApp.productRating.home.createLabel')"> Create a new Product Rating </span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && productRatings && productRatings.length === 0">
      <span v-text="$t('storeApp.productRating.home.notFound')">No productRatings found</span>
    </div>
    <div class="table-responsive" v-if="productRatings && productRatings.length > 0">
      <table class="table table-striped" aria-describedby="productRatings">
        <thead>
          <tr>
            <th scope="row"><span v-text="$t('global.field.id')">ID</span></th>
            <th scope="row"><span v-text="$t('storeApp.productRating.rating')">Rating</span></th>
            <th scope="row"><span v-text="$t('storeApp.productRating.date')">Date</span></th>
            <th scope="row"><span v-text="$t('storeApp.productRating.user')">User</span></th>
            <th scope="row"><span v-text="$t('storeApp.productRating.product')">Product</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="productRating in productRatings" :key="productRating.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ProductRatingView', params: { productRatingId: productRating.id } }">{{
                productRating.id
              }}</router-link>
            </td>
            <td>{{ productRating.rating }}</td>
            <td>{{ productRating.date ? $d(Date.parse(productRating.date), 'short') : '' }}</td>
            <td>
              {{ productRating.user ? productRating.user.login : '' }}
            </td>
            <td>
              <div v-if="productRating.product">
                <router-link :to="{ name: 'ProductView', params: { productId: productRating.product.id } }">{{
                  productRating.product.name
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'ProductRatingView', params: { productRatingId: productRating.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.view')">View</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'ProductRatingEdit', params: { productRatingId: productRating.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.edit')">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(productRating)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="$t('entity.action.delete')">Delete</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <span slot="modal-title"
        ><span id="storeApp.productRating.delete.question" data-cy="productRatingDeleteDialogHeading" v-text="$t('entity.delete.title')"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-productRating-heading" v-text="$t('storeApp.productRating.delete.question', { id: removeId })">
          Are you sure you want to delete this Product Rating?
        </p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-text="$t('entity.action.cancel')" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-productRating"
          data-cy="entityConfirmDeleteButton"
          v-text="$t('entity.action.delete')"
          v-on:click="removeProductRating()"
        >
          Delete
        </button>
      </div>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./product-rating.component.ts"></script>
