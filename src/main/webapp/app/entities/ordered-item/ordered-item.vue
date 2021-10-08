<template>
  <div>
    <h2 id="page-heading" data-cy="OrderedItemHeading">
      <span v-text="$t('storeApp.orderedItem.home.title')" id="ordered-item-heading">Ordered Items</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="$t('storeApp.orderedItem.home.refreshListLabel')">Refresh List</span>
        </button>
        <router-link :to="{ name: 'OrderedItemCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            v-if="hasAnyAuthority('ROLE_ADMIN')"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-ordered-item"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="$t('storeApp.orderedItem.home.createLabel')"> Create a new Ordered Item </span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && orderedItems && orderedItems.length === 0">
      <span v-text="$t('storeApp.orderedItem.home.notFound')">No orderedItems found</span>
    </div>
    <div class="table-responsive" v-if="orderedItems && orderedItems.length > 0">
      <table class="table table-striped" aria-describedby="orderedItems">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="$t('global.field.id')">ID</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('quantity')">
              <span v-text="$t('storeApp.orderedItem.quantity')">Quantity</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'quantity'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('totalPrice')">
              <span v-text="$t('storeApp.orderedItem.totalPrice')">Total Price</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'totalPrice'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('status')">
              <span v-text="$t('storeApp.orderedItem.status')">Status</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'status'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('product.name')">
              <span v-text="$t('storeApp.orderedItem.product')">Product</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'product.name'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('order.code')">
              <span v-text="$t('storeApp.orderedItem.order')">Order</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'order.code'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="orderedItem in orderedItems" :key="orderedItem.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'OrderedItemView', params: { orderedItemId: orderedItem.id } }">{{ orderedItem.id }}</router-link>
            </td>
            <td>{{ orderedItem.quantity }}</td>
            <td>{{ orderedItem.totalPrice }}</td>
            <td v-text="$t('storeApp.OrderedItemStatus.' + orderedItem.status)">{{ orderedItem.status }}</td>
            <td>
              <div v-if="orderedItem.product">
                <router-link :to="{ name: 'ProductView', params: { productId: orderedItem.product.id } }">{{
                  orderedItem.product.name
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="orderedItem.order">
                <router-link :to="{ name: 'ProductOrderView', params: { productOrderId: orderedItem.order.id } }">{{
                  orderedItem.order.code
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'OrderedItemView', params: { orderedItemId: orderedItem.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.view')">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'OrderedItemEdit', params: { orderedItemId: orderedItem.id } }" custom v-slot="{ navigate }">
                  <button
                    @click="navigate"
                    v-if="hasAnyAuthority('ROLE_ADMIN')"
                    class="btn btn-primary btn-sm edit"
                    data-cy="entityEditButton"
                  >
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.edit')">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(orderedItem)"
                  v-if="hasAnyAuthority('ROLE_ADMIN')"
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
        ><span id="storeApp.orderedItem.delete.question" data-cy="orderedItemDeleteDialogHeading" v-text="$t('entity.delete.title')"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-orderedItem-heading" v-text="$t('storeApp.orderedItem.delete.question', { id: removeId })">
          Are you sure you want to delete this Ordered Item?
        </p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-text="$t('entity.action.cancel')" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-orderedItem"
          data-cy="entityConfirmDeleteButton"
          v-text="$t('entity.action.delete')"
          v-on:click="removeOrderedItem()"
        >
          Delete
        </button>
      </div>
    </b-modal>
    <div v-show="orderedItems && orderedItems.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage" :change="loadPage(page)"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./ordered-item.component.ts"></script>
