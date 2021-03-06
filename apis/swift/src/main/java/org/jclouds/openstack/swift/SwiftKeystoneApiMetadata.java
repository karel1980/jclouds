/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.swift;

import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.CREDENTIAL_TYPE;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;

import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.config.AuthenticationApiModule;
import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.openstack.services.ServiceType;
import org.jclouds.openstack.swift.blobstore.config.SwiftBlobStoreContextModule;
import org.jclouds.openstack.swift.blobstore.config.TemporaryUrlExtensionModule.SwiftKeystoneTemporaryUrlExtensionModule;
import org.jclouds.openstack.swift.config.SwiftHttpApiModule.KeystoneStorageEndpointModule;
import org.jclouds.openstack.swift.config.SwiftKeystoneHttpApiModule;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@AutoService(ApiMetadata.class)
public class SwiftKeystoneApiMetadata extends SwiftApiMetadata {

   @Override
   public Builder<?, ?> toBuilder() {
      return new ConcreteBuilder().fromApiMetadata(this);
   }

   public SwiftKeystoneApiMetadata() {
      this(new ConcreteBuilder());
   }

   protected SwiftKeystoneApiMetadata(Builder<?, ?> builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = SwiftApiMetadata.defaultProperties();
      properties.setProperty(SERVICE_TYPE, ServiceType.OBJECT_STORE);
      properties.setProperty(CREDENTIAL_TYPE, CredentialTypes.PASSWORD_CREDENTIALS);
      properties.setProperty(PROPERTY_REGION, "");
      properties.remove(PROPERTY_REGIONS);
      return properties;
   }

   public abstract static class Builder<A extends CommonSwiftClient, T extends Builder<A, T>>
         extends SwiftApiMetadata.Builder<A, T> {

      protected Builder() {
         this(Class.class.cast(SwiftKeystoneClient.class));
      }

      protected Builder(Class<A> syncClient) {
         super(syncClient);
         id("swift-keystone")
               .name("OpenStack Swift with Keystone authentication")
               .identityName("${tenantName}:${userName} or ${userName}, if your keystone supports a default tenant")
               .credentialName("${password}")
               .endpointName("KeyStone base url ending in /v2.0/")
               .defaultEndpoint("http://localhost:5000/v2.0/")
               .defaultProperties(SwiftKeystoneApiMetadata.defaultProperties())
               .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                           .add(AuthenticationApiModule.class)
                                           .add(KeystoneStorageEndpointModule.class)
                                           .add(KeystoneAuthenticationModule.RegionModule.class)
                                           .add(SwiftKeystoneHttpApiModule.class)
                                           .add(SwiftBlobStoreContextModule.class)
                                           .add(SwiftKeystoneTemporaryUrlExtensionModule.class).build());
      }

      @Override
      public SwiftKeystoneApiMetadata build() {
         return new SwiftKeystoneApiMetadata(this);
      }
   }

   private static class ConcreteBuilder extends Builder<SwiftKeystoneClient, ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
}
