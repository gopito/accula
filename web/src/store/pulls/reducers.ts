import { notFetching, Wrapper } from 'store/wrapper'
import { IClone, IDiff } from 'types'
import {
  PullsActionTypes, //
  PullsState,
  SET_CLONES,
  SET_COMPARES,
  SET_DIFFS,
  SET_PULL,
  SET_PULLS
} from './types'

const decodeDiff = (wrapper: Wrapper<IDiff[]>): Wrapper<IDiff[]> => ({
  ...wrapper,
  value:
    wrapper.value &&
    wrapper.value.map(diff => ({
      baseFilename: diff.baseFilename,
      baseContent: diff.baseContent ? atob(diff.baseContent) : '',
      headFilename: diff.headFilename,
      headContent: diff.headContent ? atob(diff.headContent) : ''
    }))
})

const decodeClones = (wrapper: Wrapper<IClone[]>): Wrapper<IClone[]> => ({
  ...wrapper,
  value:
    wrapper.value &&
    wrapper.value.map(diff => ({
      id: diff.id,
      target: {
        ...diff.target,
        content: atob(diff.target.content)
      },
      source: {
        ...diff.source,
        content: atob(diff.source.content)
      }
    }))
})

const initialState: PullsState = {
  pulls: notFetching,
  pull: notFetching,
  diffs: notFetching,
  compares: notFetching,
  clones: notFetching
}

export function pullsReducer(
  state: PullsState = initialState, //
  action: PullsActionTypes
): PullsState {
  switch (action.type) {
    case SET_PULLS: {
      return {
        ...state,
        pulls: action.payload
      }
    }
    case SET_PULL: {
      return {
        ...state,
        pull: action.payload
      }
    }
    case SET_CLONES: {
      return {
        ...state,
        clones: decodeClones(action.payload)
      }
    }
    case SET_DIFFS: {
      return {
        ...state,
        diffs: decodeDiff(action.payload)
      }
    }
    case SET_COMPARES: {
      return {
        ...state,
        compares: decodeDiff(action.payload)
      }
    }
    default:
      return state
  }
}
