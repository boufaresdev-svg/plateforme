import { CanalCommunication, FrequenceContact } from "./enum.model";

export interface PreferencesClient {
  canalCommunication: CanalCommunication;
  frequenceContact: FrequenceContact;
  newsletter: boolean;
  promotions: boolean;
}