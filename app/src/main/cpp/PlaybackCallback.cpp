//
// Created by Ashfaq Salehin on 15/7/2020 AD.
//

#include "PlaybackCallback.h"

oboe::DataCallbackResult
PlaybackCallback::onAudioReady(oboe::AudioStream *audioStream, void *audioData,
                                   int32_t numFrames) {
    return processPlaybackFrame(audioStream, static_cast<int16_t *>(audioData), numFrames * audioStream->getChannelCount());
}

oboe::DataCallbackResult
PlaybackCallback::processPlaybackFrame(oboe::AudioStream *audioStream, int16_t *audioData,
                                               int32_t numFrames) {
    return oboe::DataCallbackResult::Continue;
}
