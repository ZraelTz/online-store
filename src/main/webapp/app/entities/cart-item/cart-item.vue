<template>
  <div>
    <h2 id="page-heading" data-cy="CartItemHeading">
      <span v-text="$t('storeApp.cartItem.home.title')" id="cart-item-heading">Cart Items</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="$t('storeApp.cartItem.home.refreshListLabel')">Refresh List</span>
        </button>
        <router-link :to="{ name: 'CartItemCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-cart-item"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="$t('storeApp.cartItem.home.createLabel')"> Create a new Cart Item </span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && cartItems && cartItems.length === 0">
      <span v-text="$t('storeApp.cartItem.home.notFound')">No cartItems found</span>
    </div>
    <div class="table-responsive" v-if="cartItems && cartItems.length > 0">
      <table class="table table-striped" aria-describedby="cartItems">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="$t('global.field.id')">ID</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('quantity')">
              <span v-text="$t('storeApp.cartItem.quantity')">Quantity</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'quantity'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('cart.id')">
              <span v-text="$t('storeApp.cartItem.cart')">Cart</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'cart.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('product.name')">
              <span v-text="$t('storeApp.cartItem.product')">Product</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'product.name'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="cartItem in cartItems" :key="cartItem.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'CartItemView', params: { cartItemId: cartItem.id } }">{{ cartItem.id }}</router-link>
            </td>
            <td>{{ cartItem.quantity }}</td>
            <td>
              <div v-if="cartItem.cart">
                <router-link :to="{ name: 'CartView', params: { cartId: cartItem.cart.id } }">{{ cartItem.cart.id }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="cartItem.product">
                <router-link :to="{ name: 'ProductView', params: { productId: cartItem.product.id } }">{{
                  cartItem.product.name
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'CartItemView', params: { cartItemId: cartItem.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.view')">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'CartItemEdit', params: { cartItemId: cartItem.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.edit')">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(cartItem)"
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
        ><span id="storeApp.cartItem.delete.question" data-cy="cartItemDeleteDialogHeading" v-text="$t('entity.delete.title')"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-cartItem-heading" v-text="$t('storeApp.cartItem.delete.question', { id: removeId })">
          Are you sure you want to delete this Cart Item?
        </p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-text="$t('entity.action.cancel')" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-cartItem"
          data-cy="entityConfirmDeleteButton"
          v-text="$t('entity.action.delete')"
          v-on:click="removeCartItem()"
        >
          Delete
        </button>
      </div>
    </b-modal>
    <div v-show="cartItems && cartItems.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage" :change="loadPage(page)"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./cart-item.component.ts"></script>
