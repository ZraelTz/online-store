<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="storeApp.product.home.createOrEditLabel"
          data-cy="ProductCreateUpdateHeading"
          v-text="$t('storeApp.product.home.createOrEditLabel')"
        >
          Create or edit a Product
        </h2>
        <div>
          <div class="form-group" v-if="product.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="product.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.product.name')" for="product-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="product-name"
              data-cy="name"
              :class="{ valid: !$v.product.name.$invalid, invalid: $v.product.name.$invalid }"
              v-model="$v.product.name.$model"
              required
            />
            <div v-if="$v.product.name.$anyDirty && $v.product.name.$invalid">
              <small class="form-text text-danger" v-if="!$v.product.name.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.product.material')" for="product-material">Material</label>
            <input
              type="text"
              class="form-control"
              name="material"
              id="product-material"
              data-cy="material"
              :class="{ valid: !$v.product.material.$invalid, invalid: $v.product.material.$invalid }"
              v-model="$v.product.material.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.product.description')" for="product-description">Description</label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="product-description"
              data-cy="description"
              :class="{ valid: !$v.product.description.$invalid, invalid: $v.product.description.$invalid }"
              v-model="$v.product.description.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.product.price')" for="product-price">Price</label>
            <input
              type="number"
              class="form-control"
              name="price"
              id="product-price"
              data-cy="price"
              :class="{ valid: !$v.product.price.$invalid, invalid: $v.product.price.$invalid }"
              v-model.number="$v.product.price.$model"
              required
            />
            <div v-if="$v.product.price.$anyDirty && $v.product.price.$invalid">
              <small class="form-text text-danger" v-if="!$v.product.price.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small class="form-text text-danger" v-if="!$v.product.price.min" v-text="$t('entity.validation.min', { min: 0 })">
                This field should be at least 0.
              </small>
              <small class="form-text text-danger" v-if="!$v.product.price.numeric" v-text="$t('entity.validation.number')">
                This field should be a number.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.product.productSize')" for="product-productSize">Product Size</label>
            <select
              class="form-control"
              name="productSize"
              :class="{ valid: !$v.product.productSize.$invalid, invalid: $v.product.productSize.$invalid }"
              v-model="$v.product.productSize.$model"
              id="product-productSize"
              data-cy="productSize"
              required
            >
              <option value="S" v-bind:label="$t('storeApp.ProductSize.S')">S</option>
              <option value="M" v-bind:label="$t('storeApp.ProductSize.M')">M</option>
              <option value="L" v-bind:label="$t('storeApp.ProductSize.L')">L</option>
              <option value="XL" v-bind:label="$t('storeApp.ProductSize.XL')">XL</option>
              <option value="XXL" v-bind:label="$t('storeApp.ProductSize.XXL')">XXL</option>
              <option value="XXXL" v-bind:label="$t('storeApp.ProductSize.XXXL')">XXXL</option>
            </select>
            <div v-if="$v.product.productSize.$anyDirty && $v.product.productSize.$invalid">
              <small class="form-text text-danger" v-if="!$v.product.productSize.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.product.image')" for="product-image">Image</label>
            <div>
              <img
                v-bind:src="'data:' + product.imageContentType + ';base64,' + product.image"
                style="max-height: 100px"
                v-if="product.image"
                alt="product image"
              />
              <div v-if="product.image" class="form-text text-danger clearfix">
                <span class="pull-left">{{ product.imageContentType }}, {{ byteSize(product.image) }}</span>
                <button
                  type="button"
                  v-on:click="clearInputImage('image', 'imageContentType', 'file_image')"
                  class="btn btn-secondary btn-xs pull-right"
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                </button>
              </div>
              <input
                type="file"
                ref="file_image"
                id="file_image"
                data-cy="image"
                v-on:change="setFileData($event, product, 'image', true)"
                accept="image/*"
                v-text="$t('entity.action.addimage')"
              />
            </div>
            <input
              type="hidden"
              class="form-control"
              name="image"
              id="product-image"
              data-cy="image"
              :class="{ valid: !$v.product.image.$invalid, invalid: $v.product.image.$invalid }"
              v-model="$v.product.image.$model"
            />
            <input
              type="hidden"
              class="form-control"
              name="imageContentType"
              id="product-imageContentType"
              v-model="product.imageContentType"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('storeApp.product.productCategory')" for="product-productCategory"
              >Product Category</label
            >
            <select
              class="form-control"
              id="product-productCategory"
              data-cy="productCategory"
              name="productCategory"
              v-model="product.productCategory"
            >
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  product.productCategory && productCategoryOption.id === product.productCategory.id
                    ? product.productCategory
                    : productCategoryOption
                "
                v-for="productCategoryOption in productCategories"
                :key="productCategoryOption.id"
              >
                {{ productCategoryOption.name }}
              </option>
            </select>
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
            :disabled="$v.product.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./product-update.component.ts"></script>
