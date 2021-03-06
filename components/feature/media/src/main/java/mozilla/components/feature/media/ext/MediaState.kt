/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.feature.media.ext

import android.support.v4.media.session.PlaybackStateCompat
import mozilla.components.browser.session.Session
import mozilla.components.concept.engine.media.Media
import mozilla.components.feature.media.state.MediaState

/**
 * Gets the list of [Media] associated with this [MediaState].
 */
internal fun MediaState.getMedia(): List<Media> {
    return when (this) {
        is MediaState.Playing -> media
        is MediaState.Paused -> media
        else -> emptyList()
    }
}

/**
 * Get the [Session] that caused this [MediaState] (if any).
 */
fun MediaState.getSession(): Session? {
    return when (this) {
        is MediaState.Playing -> session
        is MediaState.Paused -> session
        else -> null
    }
}

/**
 * Returns true if this [MediaState] is associated with a Custom Tab [Session].
 */
fun MediaState.isForCustomTabSession() = getSession()?.isCustomTabSession() ?: false

/**
 * Turns the [MediaState] into a [PlaybackStateCompat] to be used with a `MediaSession`.
 */
internal fun MediaState.toPlaybackState() =
    PlaybackStateCompat.Builder()
        .setActions(
            PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE)
        .setState(
            when (this) {
                is MediaState.Playing -> PlaybackStateCompat.STATE_PLAYING
                is MediaState.Paused -> PlaybackStateCompat.STATE_PAUSED
                is MediaState.None -> PlaybackStateCompat.STATE_NONE
            },
            // Time state not exposed yet:
            // https://github.com/mozilla-mobile/android-components/issues/2458
            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
            when (this) {
                // The actual playback speed is not exposed yet:
                // https://github.com/mozilla-mobile/android-components/issues/2459
                is MediaState.Playing -> 1.0f
                else -> 0.0f
            })
        .build()

/**
 * If this state is [MediaState.Playing] then pause all playing [Media].
 */
fun MediaState.pauseIfPlaying() {
    if (this is MediaState.Playing) {
        media.pause()
    }
}

/**
 * If this state is [MediaState.Paused] then resume playing all paused [Media].
 */
fun MediaState.playIfPaused() {
    if (this is MediaState.Paused) {
        media.play()
    }
}
