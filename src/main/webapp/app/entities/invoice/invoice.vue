<template>
  <div>
    <h2 id="page-heading" data-cy="InvoiceHeading">
      <span v-text="$t('storeApp.invoice.home.title')" id="invoice-heading">Invoices</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="$t('storeApp.invoice.home.refreshListLabel')">Refresh List</span>
        </button>
        <router-link :to="{ name: 'InvoiceCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-invoice"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="$t('storeApp.invoice.home.createLabel')"> Create a new Invoice </span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && invoices && invoices.length === 0">
      <span v-text="$t('storeApp.invoice.home.notFound')">No invoices found</span>
    </div>
    <div class="table-responsive" v-if="invoices && invoices.length > 0">
      <table class="table table-striped" aria-describedby="invoices">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="$t('global.field.id')">ID</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('date')">
              <span v-text="$t('storeApp.invoice.date')">Date</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'date'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('details')">
              <span v-text="$t('storeApp.invoice.details')">Details</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'details'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('status')">
              <span v-text="$t('storeApp.invoice.status')">Status</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'status'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('paymentMethod')">
              <span v-text="$t('storeApp.invoice.paymentMethod')">Payment Method</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'paymentMethod'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('paymentDate')">
              <span v-text="$t('storeApp.invoice.paymentDate')">Payment Date</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'paymentDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('paymentAmount')">
              <span v-text="$t('storeApp.invoice.paymentAmount')">Payment Amount</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'paymentAmount'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('code')">
              <span v-text="$t('storeApp.invoice.code')">Code</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'code'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('order.code')">
              <span v-text="$t('storeApp.invoice.order')">Order</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'order.code'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="invoice in invoices" :key="invoice.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'InvoiceView', params: { invoiceId: invoice.id } }">{{ invoice.id }}</router-link>
            </td>
            <td>{{ invoice.date ? $d(Date.parse(invoice.date), 'short') : '' }}</td>
            <td>{{ invoice.details }}</td>
            <td v-text="$t('storeApp.InvoiceStatus.' + invoice.status)">{{ invoice.status }}</td>
            <td v-text="$t('storeApp.PaymentMethod.' + invoice.paymentMethod)">{{ invoice.paymentMethod }}</td>
            <td>{{ invoice.paymentDate ? $d(Date.parse(invoice.paymentDate), 'short') : '' }}</td>
            <td>{{ invoice.paymentAmount }}</td>
            <td>{{ invoice.code }}</td>
            <td>
              <div v-if="invoice.order">
                <router-link :to="{ name: 'ProductOrderView', params: { productOrderId: invoice.order.id } }">{{
                  invoice.order.code
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'InvoiceView', params: { invoiceId: invoice.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.view')">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'InvoiceEdit', params: { invoiceId: invoice.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.edit')">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(invoice)"
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
        ><span id="storeApp.invoice.delete.question" data-cy="invoiceDeleteDialogHeading" v-text="$t('entity.delete.title')"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-invoice-heading" v-text="$t('storeApp.invoice.delete.question', { id: removeId })">
          Are you sure you want to delete this Invoice?
        </p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-text="$t('entity.action.cancel')" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-invoice"
          data-cy="entityConfirmDeleteButton"
          v-text="$t('entity.action.delete')"
          v-on:click="removeInvoice()"
        >
          Delete
        </button>
      </div>
    </b-modal>
    <div v-show="invoices && invoices.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage" :change="loadPage(page)"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./invoice.component.ts"></script>
