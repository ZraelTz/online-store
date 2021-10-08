<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="vendor">
        <h2 class="jh-entity-heading" data-cy="vendorDetailsHeading">
          <span v-text="$t('storeApp.vendor.detail.title')">Vendor</span> {{ vendor.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('storeApp.vendor.firstName')">First Name</span>
          </dt>
          <dd>
            <span>{{ vendor.firstName }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.vendor.lastName')">Last Name</span>
          </dt>
          <dd>
            <span>{{ vendor.lastName }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.vendor.gender')">Gender</span>
          </dt>
          <dd>
            <span v-text="$t('storeApp.Gender.' + vendor.gender)">{{ vendor.gender }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.vendor.email')">Email</span>
          </dt>
          <dd>
            <span>{{ vendor.email }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.vendor.phone')">Phone</span>
          </dt>
          <dd>
            <span>{{ vendor.phone }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.vendor.phone2')">Phone 2</span>
          </dt>
          <dd>
            <span>{{ vendor.phone2 }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.vendor.addressLine1')">Address Line 1</span>
          </dt>
          <dd>
            <span>{{ vendor.addressLine1 }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.vendor.addressLine2')">Address Line 2</span>
          </dt>
          <dd>
            <span>{{ vendor.addressLine2 }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.vendor.city')">City</span>
          </dt>
          <dd>
            <span>{{ vendor.city }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.vendor.country')">Country</span>
          </dt>
          <dd>
            <span>{{ vendor.country }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.vendor.storeName')">Store Name</span>
          </dt>
          <dd>
            <span>{{ vendor.storeName }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.vendor.businessAccountNumber')">Business Account Number</span>
          </dt>
          <dd>
            <span>{{ vendor.businessAccountNumber }}</span>
          </dd>
          <dt>
            <span v-text="$t('storeApp.vendor.user')">User</span>
          </dt>
          <dd>
            {{ vendor.user ? vendor.user.login : '' }}
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link v-if="vendor.id" :to="{ name: 'VendorEdit', params: { vendorId: vendor.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./vendor-details.component.ts"></script>
