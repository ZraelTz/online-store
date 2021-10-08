<template>
  <div>
    <h2 id="page-heading" data-cy="VendorHeading">
      <span v-text="$t('storeApp.vendor.home.title')" id="vendor-heading">Vendors</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="$t('storeApp.vendor.home.refreshListLabel')">Refresh List</span>
        </button>
        <router-link :to="{ name: 'VendorCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-vendor"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="$t('storeApp.vendor.home.createLabel')"> Create a new Vendor </span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && vendors && vendors.length === 0">
      <span v-text="$t('storeApp.vendor.home.notFound')">No vendors found</span>
    </div>
    <div class="table-responsive" v-if="vendors && vendors.length > 0">
      <table class="table table-striped" aria-describedby="vendors">
        <thead>
          <tr>
            <th scope="row"><span v-text="$t('global.field.id')">ID</span></th>
            <th scope="row"><span v-text="$t('storeApp.vendor.firstName')">First Name</span></th>
            <th scope="row"><span v-text="$t('storeApp.vendor.lastName')">Last Name</span></th>
            <th scope="row"><span v-text="$t('storeApp.vendor.gender')">Gender</span></th>
            <th scope="row"><span v-text="$t('storeApp.vendor.email')">Email</span></th>
            <th scope="row"><span v-text="$t('storeApp.vendor.phone')">Phone</span></th>
            <th scope="row"><span v-text="$t('storeApp.vendor.phone2')">Phone 2</span></th>
            <th scope="row"><span v-text="$t('storeApp.vendor.addressLine1')">Address Line 1</span></th>
            <th scope="row"><span v-text="$t('storeApp.vendor.addressLine2')">Address Line 2</span></th>
            <th scope="row"><span v-text="$t('storeApp.vendor.city')">City</span></th>
            <th scope="row"><span v-text="$t('storeApp.vendor.country')">Country</span></th>
            <th scope="row"><span v-text="$t('storeApp.vendor.storeName')">Store Name</span></th>
            <th scope="row"><span v-text="$t('storeApp.vendor.businessAccountNumber')">Business Account Number</span></th>
            <th scope="row"><span v-text="$t('storeApp.vendor.user')">User</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="vendor in vendors" :key="vendor.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'VendorView', params: { vendorId: vendor.id } }">{{ vendor.id }}</router-link>
            </td>
            <td>{{ vendor.firstName }}</td>
            <td>{{ vendor.lastName }}</td>
            <td v-text="$t('storeApp.Gender.' + vendor.gender)">{{ vendor.gender }}</td>
            <td>{{ vendor.email }}</td>
            <td>{{ vendor.phone }}</td>
            <td>{{ vendor.phone2 }}</td>
            <td>{{ vendor.addressLine1 }}</td>
            <td>{{ vendor.addressLine2 }}</td>
            <td>{{ vendor.city }}</td>
            <td>{{ vendor.country }}</td>
            <td>{{ vendor.storeName }}</td>
            <td>{{ vendor.businessAccountNumber }}</td>
            <td>
              {{ vendor.user ? vendor.user.login : '' }}
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'VendorView', params: { vendorId: vendor.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.view')">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'VendorEdit', params: { vendorId: vendor.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.edit')">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(vendor)"
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
        ><span id="storeApp.vendor.delete.question" data-cy="vendorDeleteDialogHeading" v-text="$t('entity.delete.title')"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-vendor-heading" v-text="$t('storeApp.vendor.delete.question', { id: removeId })">
          Are you sure you want to delete this Vendor?
        </p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-text="$t('entity.action.cancel')" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-vendor"
          data-cy="entityConfirmDeleteButton"
          v-text="$t('entity.action.delete')"
          v-on:click="removeVendor()"
        >
          Delete
        </button>
      </div>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./vendor.component.ts"></script>
