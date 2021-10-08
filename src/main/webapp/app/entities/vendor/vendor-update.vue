<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="storeApp.vendor.home.createOrEditLabel"
          data-cy="VendorCreateUpdateHeading"
          v-text="$t('storeApp.vendor.home.createOrEditLabel')"
        >
          Create or edit a Vendor
        </h2>
        <div>
          <div class="form-group" v-if="vendor.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="vendor.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.vendor.firstName')" for="vendor-firstName">First Name</label>
            <input
              type="text"
              class="form-control"
              name="firstName"
              id="vendor-firstName"
              data-cy="firstName"
              :class="{ valid: !$v.vendor.firstName.$invalid, invalid: $v.vendor.firstName.$invalid }"
              v-model="$v.vendor.firstName.$model"
              required
            />
            <div v-if="$v.vendor.firstName.$anyDirty && $v.vendor.firstName.$invalid">
              <small class="form-text text-danger" v-if="!$v.vendor.firstName.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.vendor.lastName')" for="vendor-lastName">Last Name</label>
            <input
              type="text"
              class="form-control"
              name="lastName"
              id="vendor-lastName"
              data-cy="lastName"
              :class="{ valid: !$v.vendor.lastName.$invalid, invalid: $v.vendor.lastName.$invalid }"
              v-model="$v.vendor.lastName.$model"
              required
            />
            <div v-if="$v.vendor.lastName.$anyDirty && $v.vendor.lastName.$invalid">
              <small class="form-text text-danger" v-if="!$v.vendor.lastName.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.vendor.gender')" for="vendor-gender">Gender</label>
            <select
              class="form-control"
              name="gender"
              :class="{ valid: !$v.vendor.gender.$invalid, invalid: $v.vendor.gender.$invalid }"
              v-model="$v.vendor.gender.$model"
              id="vendor-gender"
              data-cy="gender"
              required
            >
              <option value="MALE" v-bind:label="$t('storeApp.Gender.MALE')">MALE</option>
              <option value="FEMALE" v-bind:label="$t('storeApp.Gender.FEMALE')">FEMALE</option>
              <option value="OTHER" v-bind:label="$t('storeApp.Gender.OTHER')">OTHER</option>
            </select>
            <div v-if="$v.vendor.gender.$anyDirty && $v.vendor.gender.$invalid">
              <small class="form-text text-danger" v-if="!$v.vendor.gender.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.vendor.email')" for="vendor-email">Email</label>
            <input
              type="text"
              class="form-control"
              name="email"
              id="vendor-email"
              data-cy="email"
              :class="{ valid: !$v.vendor.email.$invalid, invalid: $v.vendor.email.$invalid }"
              v-model="$v.vendor.email.$model"
              required
            />
            <div v-if="$v.vendor.email.$anyDirty && $v.vendor.email.$invalid">
              <small class="form-text text-danger" v-if="!$v.vendor.email.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.vendor.email.pattern"
                v-text="$t('entity.validation.pattern', { pattern: 'Email' })"
              >
                This field should follow pattern for "Email".
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.vendor.phone')" for="vendor-phone">Phone</label>
            <input
              type="text"
              class="form-control"
              name="phone"
              id="vendor-phone"
              data-cy="phone"
              :class="{ valid: !$v.vendor.phone.$invalid, invalid: $v.vendor.phone.$invalid }"
              v-model="$v.vendor.phone.$model"
              required
            />
            <div v-if="$v.vendor.phone.$anyDirty && $v.vendor.phone.$invalid">
              <small class="form-text text-danger" v-if="!$v.vendor.phone.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.vendor.phone2')" for="vendor-phone2">Phone 2</label>
            <input
              type="text"
              class="form-control"
              name="phone2"
              id="vendor-phone2"
              data-cy="phone2"
              :class="{ valid: !$v.vendor.phone2.$invalid, invalid: $v.vendor.phone2.$invalid }"
              v-model="$v.vendor.phone2.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.vendor.addressLine1')" for="vendor-addressLine1">Address Line 1</label>
            <input
              type="text"
              class="form-control"
              name="addressLine1"
              id="vendor-addressLine1"
              data-cy="addressLine1"
              :class="{ valid: !$v.vendor.addressLine1.$invalid, invalid: $v.vendor.addressLine1.$invalid }"
              v-model="$v.vendor.addressLine1.$model"
              required
            />
            <div v-if="$v.vendor.addressLine1.$anyDirty && $v.vendor.addressLine1.$invalid">
              <small class="form-text text-danger" v-if="!$v.vendor.addressLine1.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.vendor.addressLine2')" for="vendor-addressLine2">Address Line 2</label>
            <input
              type="text"
              class="form-control"
              name="addressLine2"
              id="vendor-addressLine2"
              data-cy="addressLine2"
              :class="{ valid: !$v.vendor.addressLine2.$invalid, invalid: $v.vendor.addressLine2.$invalid }"
              v-model="$v.vendor.addressLine2.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.vendor.city')" for="vendor-city">City</label>
            <input
              type="text"
              class="form-control"
              name="city"
              id="vendor-city"
              data-cy="city"
              :class="{ valid: !$v.vendor.city.$invalid, invalid: $v.vendor.city.$invalid }"
              v-model="$v.vendor.city.$model"
              required
            />
            <div v-if="$v.vendor.city.$anyDirty && $v.vendor.city.$invalid">
              <small class="form-text text-danger" v-if="!$v.vendor.city.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.vendor.country')" for="vendor-country">Country</label>
            <input
              type="text"
              class="form-control"
              name="country"
              id="vendor-country"
              data-cy="country"
              :class="{ valid: !$v.vendor.country.$invalid, invalid: $v.vendor.country.$invalid }"
              v-model="$v.vendor.country.$model"
              required
            />
            <div v-if="$v.vendor.country.$anyDirty && $v.vendor.country.$invalid">
              <small class="form-text text-danger" v-if="!$v.vendor.country.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.vendor.storeName')" for="vendor-storeName">Store Name</label>
            <input
              type="text"
              class="form-control"
              name="storeName"
              id="vendor-storeName"
              data-cy="storeName"
              :class="{ valid: !$v.vendor.storeName.$invalid, invalid: $v.vendor.storeName.$invalid }"
              v-model="$v.vendor.storeName.$model"
              required
            />
            <div v-if="$v.vendor.storeName.$anyDirty && $v.vendor.storeName.$invalid">
              <small class="form-text text-danger" v-if="!$v.vendor.storeName.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.vendor.businessAccountNumber')" for="vendor-businessAccountNumber"
              >Business Account Number</label
            >
            <input
              type="number"
              class="form-control"
              name="businessAccountNumber"
              id="vendor-businessAccountNumber"
              data-cy="businessAccountNumber"
              :class="{ valid: !$v.vendor.businessAccountNumber.$invalid, invalid: $v.vendor.businessAccountNumber.$invalid }"
              v-model.number="$v.vendor.businessAccountNumber.$model"
              required
            />
            <div v-if="$v.vendor.businessAccountNumber.$anyDirty && $v.vendor.businessAccountNumber.$invalid">
              <small
                class="form-text text-danger"
                v-if="!$v.vendor.businessAccountNumber.required"
                v-text="$t('entity.validation.required')"
              >
                This field is required.
              </small>
              <small class="form-text text-danger" v-if="!$v.vendor.businessAccountNumber.numeric" v-text="$t('entity.validation.number')">
                This field should be a number.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.vendor.user')" for="vendor-user">User</label>
            <select class="form-control" id="vendor-user" data-cy="user" name="user" v-model="vendor.user" required>
              <option v-if="!vendor.user" v-bind:value="null" selected></option>
              <option
                v-bind:value="vendor.user && userOption.id === vendor.user.id ? vendor.user : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.login }}
              </option>
            </select>
          </div>
          <div v-if="$v.vendor.user.$anyDirty && $v.vendor.user.$invalid">
            <small class="form-text text-danger" v-if="!$v.vendor.user.required" v-text="$t('entity.validation.required')">
              This field is required.
            </small>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.cancel')">Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="$v.vendor.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./vendor-update.component.ts"></script>
