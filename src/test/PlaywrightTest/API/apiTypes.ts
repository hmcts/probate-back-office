export interface EventDetails {
  id: string;
  summary?: string;
  description?: string;
}

export interface CaseDetails {
  id: number;
  jurisdiction?: string;
  case_type_id?: string;
  created_date?: string;
  last_modified?: string;
  state?: string;
  version?: number;
  security_classification?: string;
  case_data?: Record<string, unknown>;
}

export interface StartEventResponse {
  case_details: CaseDetails;
  event_id: string;
  token: string;
}

export interface DataContent {
  event: EventDetails;
  data: Record<string, unknown>;
  event_token: string;
  ignore_warning: boolean;
  security_classification?: string;
  supplementary_data_request?: Record<string, Record<string, unknown>>;
  case_reference?: string;
}

export interface SubmitEventResponse {
  id: number;
  jurisdiction?: string;
  case_type_id?: string;
  state: string;
  created_date?: string;
  last_modified?: string;
  last_state_modified_date?: string;
  data?: Record<string, unknown>;
  security_classification?: string;
  data_classification?: Record<string, unknown>;
  callback_response_status?: string;
  version?: number;
}

export interface CaseEventResult {
  before: SubmitEventResponse;
  startEvent: StartEventResponse;
  payload: DataContent;
  submitResponse: SubmitEventResponse;
  after: SubmitEventResponse;
}
