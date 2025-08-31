import nl.thedutchruben.mccore.utils.animation.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AnimationModuleTest {
    
    @Mock
    private Plugin mockPlugin;
    
    @Mock
    private World mockWorld;
    
    @Mock
    private Location mockLocation;
    
    private AnimationManager animationManager;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockLocation.getWorld()).thenReturn(mockWorld);
        when(mockLocation.clone()).thenReturn(mockLocation);
        animationManager = AnimationManager.getInstance(mockPlugin);
    }
    
    @AfterEach
    void tearDown() {
        AnimationManager.cleanup(mockPlugin);
    }
    
    @Test
    void testAnimationBuilderCreatesAnimation() {
        Animation animation = new AnimationBuilder(mockPlugin)
            .duration(50)
            .interval(2)
            .loop(true)
            .onTick(tick -> {})
            .build();
        
        assertNotNull(animation);
        assertEquals(50, animation.getDuration());
        assertEquals(2, animation.getInterval());
        assertTrue(animation.isLooping());
        assertFalse(animation.isRunning());
    }
    
    @Test
    void testAnimationManagerRegisterAndRetrieve() {
        Animation animation = new AnimationBuilder(mockPlugin)
            .duration(20)
            .interval(1)
            .build();
        
        String id = animationManager.registerAnimation(animation);
        assertNotNull(id);
        
        Animation retrieved = animationManager.getAnimation(id);
        assertEquals(animation, retrieved);
    }
    
    @Test
    void testAnimationManagerNamedRegistration() {
        Animation animation = new AnimationBuilder(mockPlugin)
            .duration(30)
            .interval(1)
            .build();
        
        animationManager.registerAnimation("test_animation", animation);
        
        Animation retrieved = animationManager.getAnimation("test_animation");
        assertEquals(animation, retrieved);
    }
    
    @Test
    void testEasingFunctions() {
        assertEquals(0.0, EasingFunction.LINEAR.apply(0.0), 0.001);
        assertEquals(1.0, EasingFunction.LINEAR.apply(1.0), 0.001);
        assertEquals(0.5, EasingFunction.LINEAR.apply(0.5), 0.001);
        
        assertTrue(EasingFunction.EASE_IN.apply(0.5) < 0.5);
        assertTrue(EasingFunction.EASE_OUT.apply(0.5) > 0.5);
        
        assertEquals(0.0, EasingFunction.EASE_IN.apply(0.0), 0.001);
        assertEquals(1.0, EasingFunction.EASE_IN.apply(1.0), 0.001);
    }
    
    @Test
    void testKeyframeCreation() {
        Location testLocation = mock(Location.class);
        when(testLocation.clone()).thenReturn(testLocation);
        
        Keyframe<Location> keyframe = new Keyframe<>(10, testLocation, EasingFunction.EASE_IN);
        
        assertEquals(10, keyframe.getTick());
        assertEquals(testLocation, keyframe.getValue());
        assertEquals(EasingFunction.EASE_IN, keyframe.getEasing());
    }
    
    @Test
    void testParticleAnimationCircleCreation() {
        ParticleAnimation.Circle circleAnimation = new ParticleAnimation.Circle(
            mockPlugin, mockLocation, Particle.FLAME, 2.0, 10, 50, 2
        );
        
        assertNotNull(circleAnimation);
        assertEquals(50, circleAnimation.getDuration());
        assertEquals(2, circleAnimation.getInterval());
        assertFalse(circleAnimation.isRunning());
    }
    
    @Test
    void testAnimationManagerActiveCount() {
        assertEquals(0, animationManager.getActiveAnimationCount());
        
        Animation animation1 = new AnimationBuilder(mockPlugin)
            .duration(100)
            .interval(1)
            .build();
        Animation animation2 = new AnimationBuilder(mockPlugin)
            .duration(100)
            .interval(1)
            .build();
        
        String id1 = animationManager.registerAnimation(animation1);
        String id2 = animationManager.registerAnimation(animation2);
        
        assertEquals(0, animationManager.getActiveAnimationCount());
    }
    
    @Test
    void testAnimationManagerStopAll() {
        Animation animation1 = new AnimationBuilder(mockPlugin)
            .duration(100)
            .interval(1)
            .build();
        Animation animation2 = new AnimationBuilder(mockPlugin)
            .duration(100)
            .interval(1)
            .build();
        
        animationManager.registerAnimation("anim1", animation1);
        animationManager.registerAnimation("anim2", animation2);
        
        animationManager.stopAll();
        
        assertFalse(animationManager.isAnimationRunning("anim1"));
        assertFalse(animationManager.isAnimationRunning("anim2"));
    }
    
    @Test
    void testAnimationManagerRemoveAnimation() {
        Animation animation = new AnimationBuilder(mockPlugin)
            .duration(50)
            .interval(1)
            .build();
        
        String id = animationManager.registerAnimation(animation);
        assertNotNull(animationManager.getAnimation(id));
        
        animationManager.removeAnimation(id);
        assertNull(animationManager.getAnimation(id));
    }
}